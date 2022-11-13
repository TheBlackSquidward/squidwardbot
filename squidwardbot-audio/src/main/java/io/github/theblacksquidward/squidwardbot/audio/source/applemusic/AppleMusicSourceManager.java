package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import io.github.theblacksquidward.squidwardbot.audio.source.delegating.DelegatingSourceManager;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AppleMusicSourceManager extends DelegatingSourceManager implements HttpConfigurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppleMusicSourceManager.class);

    private static final HttpInterfaceManager HTTP_INTERFACE_MANAGER = HttpClientTools.createDefaultThreadLocalManager();

    public static final Pattern URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?music\\.apple\\.com/(?<countrycode>[a-zA-Z]{2}/)?(?<type>album|playlist|artist)(/[a-zA-Z0-9\\-]+)?/(?<identifier>[a-zA-Z0-9.]+)(\\?i=(?<identifier2>\\d+))?");
    public static final String SEARCH_PREFIX = "apsearch:";
    public static final int MAX_PAGE_ITEMS = 300;

    private final String countryCode;

    private String token;
    private Instant tokenExpire;

    public AppleMusicSourceManager(AudioPlayerManager audioPlayerManager, String countryCode) {
        super(audioPlayerManager);
        if(countryCode == null || countryCode.isEmpty()) {
            countryCode = "us";
        }
        this.countryCode = countryCode;
    }

    public AppleMusicSourceManager(AudioPlayerManager audioPlayerManager) {
        this(audioPlayerManager, "us");
    }

    @Override
    public String getSourceName() {
        return "applemusic";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager audioPlayerManager, @NotNull AudioReference audioReference) {
        try {
            if (audioReference.identifier.startsWith(SEARCH_PREFIX)) {
                return getFirstSearchResultAsTrack(audioReference.identifier.substring(SEARCH_PREFIX.length()).trim());
            }
            Matcher matcher = URL_PATTERN.matcher(audioReference.identifier);
            if (!matcher.find()) return null;

            String identifier = matcher.group("identifier");
            String type = matcher.group("type");
            switch (type) {
                case "song":
                    return getSong(identifier);
                case "playlist":
                    return getPlaylist(identifier);
                case "album":
                    String identifier2 = matcher.group("identifier2");
                    if (identifier2 == null || identifier2.isEmpty()) {
                        return getAlbum(identifier);
                    }
                    return getSong(identifier2);
                case "artist":
                    return getArtist(identifier);
                default:
                    throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestToken() throws IOException {
        HttpGet request = new HttpGet("https://music.apple.com");
        try (CloseableHttpResponse response = this.HTTP_INTERFACE_MANAGER.getInterface().execute(request)) {
            Document document = Jsoup.parse(response.getEntity().getContent(), null, "");
            Element element = document.selectFirst("meta[name=desktop-music-app/config/environment]");
            if (element == null) throw new IOException("Could not find token");
            token = JsonBrowser.parse(URLDecoder.decode(element.attr("content"), StandardCharsets.UTF_8)).get("MEDIA_API").get("token").text();
            tokenExpire = Instant.ofEpochSecond(JsonBrowser.parse(new String(Base64.getDecoder().decode(this.token.split("\\.")[1]))).get("exp").asLong(0));
        }
    }

    public String getToken() throws IOException {
        if(token == null || tokenExpire == null || tokenExpire.isBefore(Instant.now())) {
            requestToken();
        }
        return token;
    }

    public JsonBrowser getJson(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.addHeader("Authorization", "Bearer " + getToken());
        request.addHeader("Origin", "https://music.apple.com");
        return HttpClientTools.fetchResponseAsJson(this.HTTP_INTERFACE_MANAGER.getInterface(), request);
    }

    private AudioItem getFirstSearchResultAsTrack(String identifier) throws IOException {
        List<AudioTrack> searchResults = getSearchResults(identifier);
        return searchResults.isEmpty() ? AudioReference.NO_TRACK : searchResults.get(0);
    }

    @SuppressWarnings("unused")
    private AudioItem getAllSearchResultsAsPlaylist(String identifier) throws IOException {
        List<AudioTrack> searchResults = getSearchResults(identifier);
        return searchResults.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist("Apple Music Search Results For: " + identifier, searchResults, null, true);
    }

    private List<AudioTrack> getSearchResults(String identifier) throws IOException {
        JsonBrowser json = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/search?term=" + URLEncoder.encode(identifier, StandardCharsets.UTF_8) + "&limit=" + 25);
        return json == null || json.get("results").get("songs").get("data").values().isEmpty() ? Collections.emptyList() : parseTracks(json.get("results").get("songs"));
    }

    private AudioItem getAlbum(String identifier) throws IOException {
        JsonBrowser json = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/albums/" + identifier);
        if (json == null) return AudioReference.NO_TRACK;

        List<AudioTrack> tracks = new ArrayList<>();
        int offset = 0;
        JsonBrowser page = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/albums/" + identifier + "/tracks?limit=" + MAX_PAGE_ITEMS + "&offset=" + offset);

        while(page.get("next").text() != null) {
            offset += MAX_PAGE_ITEMS;
            tracks.addAll(parseTracks(page));
            page = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/albums/" + identifier + "/tracks?limit=" + MAX_PAGE_ITEMS + "&offset=" + offset);
        }

        return tracks.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist(json.get("data").index(0).get("attributes").get("name").text(), tracks, null, false);
    }

    private AudioItem getSong(String identifier) throws IOException {
        JsonBrowser json = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/songs/" + identifier);
        return json == null ? AudioReference.NO_TRACK : parseTrack(json.get("data").index(0));
    }

    private AudioItem getPlaylist(String identifier) throws IOException {
        JsonBrowser json = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/playlists/" + identifier);
        if (json == null) return AudioReference.NO_TRACK;

        List<AudioTrack> tracks = new ArrayList<>();
        int offset = 0;
        JsonBrowser page = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/playlists/" + identifier + "/tracks?limit=" + MAX_PAGE_ITEMS + "&offset=" + offset);

        while(page.get("next").text() != null) {
            offset += MAX_PAGE_ITEMS;
            tracks.addAll(parseTracks(page));
            page = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/playlists/" + identifier + "/tracks?limit=" + MAX_PAGE_ITEMS + "&offset=" + offset);
        }

        return tracks.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist(json.get("data").index(0).get("attributes").get("name").text(), tracks, null, false);
    }

    private AudioItem getArtist(String identifier) throws IOException {
        JsonBrowser json = getJson("https://api.music.apple.com/v1/catalog/" + countryCode + "/artists/" + identifier + "/view/top-songs");
        return json == null || json.get("data").values().isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist(json.get("data").index(0).get("attributes").get("artistName").text() + "'s Top Tracks", parseTracks(json), null, false);
    }

    private List<AudioTrack> parseTracks(@NotNull JsonBrowser json) {
        return json.get("data").values().stream()
                .map(this::parseTrack)
                .collect(Collectors.toList());
    }

    private @NotNull AudioTrack parseTrack(@NotNull JsonBrowser json) {
        JsonBrowser attributes = json.get("attributes");
        JsonBrowser artwork = attributes.get("artwork");
        return new AppleMusicAudioTrack(
                new AudioTrackInfo(
                        attributes.get("name").text(),
                        attributes.get("artistName").text(),
                        attributes.get("durationInMillis").asLong(0),
                        json.get("id").text(),
                        false,
                        attributes.get("url").text()
                ),
                attributes.get("isrc").text(),
                artwork.get("url").text().replace("{w}", artwork.get("width").text()).replace("{h}", artwork.get("height").text()),
                this);
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo audioTrackInfo, DataInput dataInput) throws IOException {
        return new AppleMusicAudioTrack(audioTrackInfo,
                DataFormatTools.readNullableText(dataInput),
                DataFormatTools.readNullableText(dataInput),
                this
        );
    }

    @Override
    public void shutdown() {
        ExceptionTools.closeWithWarnings(HTTP_INTERFACE_MANAGER);
    }

    @Override
    public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
        HTTP_INTERFACE_MANAGER.configureRequests(configurator);
    }

    @Override
    public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
        HTTP_INTERFACE_MANAGER.configureBuilder(configurator);
    }

}
