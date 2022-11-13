package io.github.theblacksquidward.squidwardbot.audio.source.deezer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DeezerSourceManager implements AudioSourceManager, HttpConfigurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeezerSourceManager.class);
    private static final HttpInterfaceManager httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();

    public static final Pattern URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?deezer\\.com/(?<countrycode>[a-zA-Z]{2}/)?(?<type>track|album|playlist|artist)/(?<identifier>[0-9]+)");
    public static final String SEARCH_PREFIX = "dzsearch:";
    public static final String ISRC_SEARCH_PREFIX = "dzisrc:";
    public static final String SHARE_URL = "https://deezer.page.link/";
    public static final String PUBLIC_API_BASE = "https://api.deezer.com/2.0";
    public static final String PRIVATE_API_BASE = "https://www.deezer.com/ajax/gw-light.php";
    public static final String MEDIA_BASE = "https://media.deezer.com/v1";

    private final String masterDecryptionKey;

    public DeezerSourceManager(String masterDecryptionKey) {
        if (masterDecryptionKey == null || masterDecryptionKey.isEmpty()) {
            throw new IllegalArgumentException("Deezer master key must be set");
        }
        this.masterDecryptionKey = masterDecryptionKey;
    }

    public String getMasterDecryptionKey() {
        return masterDecryptionKey;
    }

    @Override
    public String getSourceName() {
        return "deezer";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference audioReference) {
        try {
            if(audioReference.identifier.startsWith(SEARCH_PREFIX)) {
                return getFirstSearchResultAsTrack(audioReference.identifier.substring(SEARCH_PREFIX.length()));
            }
            if(audioReference.identifier.startsWith(ISRC_SEARCH_PREFIX)) {
                return getTrackByISRC(audioReference.identifier.substring(ISRC_SEARCH_PREFIX.length()));
            }

            //TODO share url

            Matcher matcher = URL_PATTERN.matcher(audioReference.identifier);
            if (!matcher.find()) {
                return null;
            }
            String identifier = matcher.group("identifier");
            String type = matcher.group("type");
            return switch (type) {
                case "track" -> getTrack(identifier);
                case "playlist" -> getPlaylist(identifier);
                case "album" -> getAlbum(identifier);
                case "artist" -> getArtist(identifier);
                default -> throw new IllegalArgumentException();
            };
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public JsonBrowser getJson(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.setHeader("Accept", "application/json");
        return HttpClientTools.fetchResponseAsJson(httpInterfaceManager.getInterface(), request);
    }

    private AudioItem getFirstSearchResultAsTrack(String identifier) throws IOException {
        List<AudioTrack> searchResults = getSearchResults(identifier);
        return searchResults.isEmpty() ? AudioReference.NO_TRACK : searchResults.get(0);
    }

    @SuppressWarnings("unused")
    private AudioItem getAllSearchResultsAsPlaylist(String identifier) throws IOException {
        List<AudioTrack> searchResults = getSearchResults(identifier);
        return searchResults.isEmpty() ? AudioReference.NO_TRACK : new BasicAudioPlaylist("Search results for: " + identifier, searchResults, null, true);
    }

    private List<AudioTrack> getSearchResults(String identifier) throws IOException {
        JsonBrowser json = getJson(PUBLIC_API_BASE + "/search?q=" + URLEncoder.encode(identifier, StandardCharsets.UTF_8));
        if (json == null || json.get("data").values().isEmpty()) {
            return Collections.emptyList();
        }
        return parseTracks(json);
    }

    private AudioItem getTrackByISRC(String isrc) throws IOException {
        JsonBrowser json = getJson(PUBLIC_API_BASE + "/track/isrc:" + isrc);
        return json == null || json.get("id").isNull() ? AudioReference.NO_TRACK : parseTrack(json);
    }

    private AudioItem getTrack(String identifier) throws IOException {
        JsonBrowser json = getJson(PUBLIC_API_BASE + "/track/" + identifier);
        return json == null ? AudioReference.NO_TRACK : parseTrack(json);
    }

    private AudioItem getAlbum(String identifier) throws IOException {
        JsonBrowser json = getJson(PUBLIC_API_BASE + "/album/" + identifier);
        if (json == null || json.get("tracks").get("data").values().isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return new BasicAudioPlaylist(json.get("title").text(), this.parseTracks(json.get("tracks")), null, false);
    }

    private AudioItem getPlaylist(String identifier) throws IOException {
        JsonBrowser json = getJson(PUBLIC_API_BASE + "/playlist/" + identifier);
        if (json == null || json.get("tracks").get("data").values().isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return new BasicAudioPlaylist(json.get("title").text(), this.parseTracks(json.get("tracks")), null, false);
    }

    private AudioItem getArtist(String identifier) throws IOException {
        JsonBrowser json = getJson(PUBLIC_API_BASE + "/artist/" + identifier + "/top?limit=50");
        if (json == null || json.get("tracks").get("data").values().isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return new BasicAudioPlaylist(json.get("data").index(0).get("artist").get("name").text() + "'s Top Tracks", this.parseTracks(json), null, false);
    }

    private List<AudioTrack> parseTracks(JsonBrowser json) {
        return json.get("data").values().stream()
                .filter(trackJson -> trackJson.get("type").text().equals("track"))
                .map(this::parseTrack)
                .collect(Collectors.toList());
    }

    private AudioTrack parseTrack(JsonBrowser json) {
        String id = json.get("id").text();
        return new DeezerAudioTrack(new AudioTrackInfo(
                json.get("title").text(),
                json.get("artist").get("name").text(),
                json.get("duration").as(Long.class) * 1000,
                id,
                false,
                "https://deezer.com/track/" + id
                ),
                json.get("isrc").text(),
                json.get("album").get("cover_x1").text(),
                this
        );
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        DeezerAudioTrack deezerAudioTrack = ((DeezerAudioTrack) track);
        DataFormatTools.writeNullableText(output, deezerAudioTrack.getIsrc());
        DataFormatTools.writeNullableText(output, deezerAudioTrack.getArtworkUrl());
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo audioTrackInfo, DataInput dataInput) throws IOException {
        return new DeezerAudioTrack(audioTrackInfo,
                DataFormatTools.readNullableText(dataInput),
                DataFormatTools.readNullableText(dataInput),
                this
        );
    }

    @Override
    public void shutdown() {
        ExceptionTools.closeWithWarnings(httpInterfaceManager);
    }

    @Override
    public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
        httpInterfaceManager.configureRequests(configurator);
    }

    @Override
    public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
        httpInterfaceManager.configureBuilder(configurator);
    }

    public HttpInterface getHttpInterface() {
        return httpInterfaceManager.getInterface();
    }

}
