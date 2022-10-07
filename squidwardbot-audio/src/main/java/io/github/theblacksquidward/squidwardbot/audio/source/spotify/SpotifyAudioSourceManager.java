package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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

public class SpotifyAudioSourceManager implements AudioSourceManager, HttpConfigurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioSourceManager.class);

    public static final Pattern URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist|artist)/(?<identifier>[a-zA-Z0-9-_]+)");
    public static final String SEARCH_PREFIX = "spsearch:";
    public static final String COUNTRY_CODE = "GB";
    public static final int PLAYLIST_MAX_PAGE_ITEMS = 100;
    public static final int ALBUM_MAX_PAGE_ITEMS = 50;

    private final HttpInterfaceManager httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();

    private final AudioPlayerManager audioPlayerManager;

    private final String spotifyClientId;
    private final String spotifyClientSecret;

    private String token;
    private Instant tokenExpire;

    public SpotifyAudioSourceManager(AudioPlayerManager audioPlayerManager, String spotifyClientId, String spotifyClientSecret) {
        this.audioPlayerManager = audioPlayerManager;
        if(spotifyClientId == null || spotifyClientId.isEmpty()){
            throw new IllegalArgumentException("Spotify client id must be set");
        }
        this.spotifyClientId = spotifyClientId;

        if(spotifyClientSecret == null || spotifyClientSecret.isEmpty()){
            throw new IllegalArgumentException("Spotify secret must be set");
        }
        this.spotifyClientSecret = spotifyClientSecret;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    @Override
    public String getSourceName() {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference audioReference) {
        try {
            if(audioReference.identifier.startsWith(SEARCH_PREFIX)) {
                return this.getFirstSearchResultAsTrack(audioReference.identifier.substring(SEARCH_PREFIX.length()).trim());
            }
            Matcher matcher = URL_PATTERN.matcher(audioReference.identifier);
            if (!matcher.find()) {
                return null;
            }
            String identifier = matcher.group("identifier");
            String type = matcher.group("type");
            return switch (type) {
                case "track" -> this.getTrack(identifier);
                case "playlist" -> this.getPlaylist(identifier);
                case "album" -> this.getAlbum(identifier);
                case "artist" -> this.getArtist(identifier);
                default -> throw new IllegalArgumentException();
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestToken() throws IOException {
        HttpPost request = new HttpPost("https://accounts.spotify.com/api/token");
        request.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((spotifyClientId + ":" + spotifyClientSecret).getBytes(StandardCharsets.UTF_8)));
        request.setEntity(new UrlEncodedFormEntity(List.of(new BasicNameValuePair("grant_type", "client_credentials")), StandardCharsets.UTF_8));

        JsonBrowser json = HttpClientTools.fetchResponseAsJson(this.httpInterfaceManager.getInterface(), request);
        token = json.get("access_token").text();
        tokenExpire = Instant.now().plusSeconds(json.get("expires_in").asLong(0));
    }

    public String getToken() throws IOException {
        if(this.token == null || this.tokenExpire == null || this.tokenExpire.isBefore(Instant.now())) {
            requestToken();
        }
        return token;
    }

    public JsonBrowser getJson(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);
        request.addHeader("Authorization", "Bearer " + getToken());
        return HttpClientTools.fetchResponseAsJson(httpInterfaceManager.getInterface(), request);
    }

    private AudioItem getFirstSearchResultAsTrack(String identifier) throws IOException {
        List<AudioTrack> result = getSearchResults(identifier);
        if(result.isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return result.get(0);
    }

    private AudioItem getAllSearchResultsAsPlaylist(String identifier) throws IOException {
        List<AudioTrack> result = getSearchResults(identifier);
        if(result.isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return new BasicAudioPlaylist("Search results for: " + identifier, result, null, true);
    }

    private List<AudioTrack> getSearchResults(String identifier) throws IOException {
        JsonBrowser json = this.getJson("https://api.spotify.com/v1/search?q=" + URLEncoder.encode(identifier, StandardCharsets.UTF_8) + "&type=track,album,playlist,artist");
        if(json == null) {
            return Collections.emptyList();
        }
        return parseSpotifySearch(json);
    }

    private AudioItem getTrack(String identifier) throws IOException {
        JsonBrowser json = this.getJson("https://api.spotify.com/v1/tracks/" + identifier);
        if(json == null){
            return AudioReference.NO_TRACK;
        }
        return parseSpotifyTrack(json);
    }

    private AudioItem getPlaylist(String identifier) throws IOException {
        JsonBrowser json = this.getJson("https://api.spotify.com/v1/playlists/" + identifier);
        if(json == null){
            return AudioReference.NO_TRACK;
        }

        List<AudioTrack> tracks = new ArrayList<>();
        JsonBrowser page;
        var offset = 0;
        do{
            page = this.getJson("https://api.spotify.com/v1/playlists/" + identifier + "/tracks?limit=" + PLAYLIST_MAX_PAGE_ITEMS + "&offset=" + offset);
            offset += PLAYLIST_MAX_PAGE_ITEMS;

            tracks.addAll(parseSpotifyPlaylist(page));
        }
        while(page.get("next").text() != null);

        return new BasicAudioPlaylist(json.get("data").index(0).get("attributes").get("name").text(), tracks, null, false);
    }

    private AudioItem getAlbum(String identifier) throws IOException {
        JsonBrowser json = this.getJson("https://api.spotify.com/v1/albums/" + identifier);
        if(json == null){
            return AudioReference.NO_TRACK;
        }

        List<AudioTrack> tracks = new ArrayList<>();
        JsonBrowser page;
        var offset = 0;
        do{
            page = this.getJson("https://api.spotify.com/v1/albums/" + identifier + "/tracks?limit=" + ALBUM_MAX_PAGE_ITEMS + "&offset=" + offset);
            offset += ALBUM_MAX_PAGE_ITEMS;

            tracks.addAll(parseSpotifyAlbum(page));
        }
        while(page.get("next").text() != null);

        return new BasicAudioPlaylist(json.get("data").index(0).get("attributes").get("name").text(), tracks, null, false);
    }

    private AudioItem getArtist(String identifier) throws IOException {
        JsonBrowser json = this.getJson("https://api.spotify.com/v1/artists/" + identifier + "/top-tracks?market=" + COUNTRY_CODE);
        if(json == null) {
            return AudioReference.NO_TRACK;
        }
        return new BasicAudioPlaylist(json.get("tracks").index(0).get("artists").index(0).get("name").text() + "'s Top Tracks", parseSpotifyTracks(json), null, false);
    }

    private List<AudioTrack> parseSpotifySearch(JsonBrowser json) {
        List<AudioTrack> tracks = new ArrayList<>();
        for(JsonBrowser value : json.get("tracks").get("items").values()) {
            tracks.add(parseSpotifyTrack(value));
        }
        return tracks;
    }

    private List<AudioTrack> parseSpotifyPlaylist(JsonBrowser json) {
        List<AudioTrack> tracks = new ArrayList<>();
        for(JsonBrowser value : json.get("items").values()){
            tracks.add(parseSpotifyTrack(value.get("track")));
        }
        return tracks;
    }

    private List<AudioTrack> parseSpotifyAlbum(JsonBrowser json) {
        List<AudioTrack> tracks = new ArrayList<>();
        for(JsonBrowser value : json.get("items").values()){
            tracks.add(parseSpotifyTrack(value));
        }
        return tracks;
    }

    private List<AudioTrack> parseSpotifyTracks(JsonBrowser json) {
        List<AudioTrack> tracks = new ArrayList<>();
        for(JsonBrowser value : json.get("tracks").values()) {
            tracks.add(parseSpotifyTrack(value));
        }
        return tracks;
    }

    private AudioTrack parseSpotifyTrack(JsonBrowser json) {
        return new SpotifyAudioTrack(
                new AudioTrackInfo(
                        json.get("name").text(),
                        json.get("artists").index(0).get("name").text(),
                        json.get("duration_ms").asLong(0),
                        json.get("id").text(),
                        false,
                        json.get("external_urls").get("spotify").text(),
                        json.get("album").get("images").index(0).get("url").text(),
                        json.get("external_ids").get("isrc").text()
                ), this);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        // Nothing special to decode
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return new SpotifyAudioTrack(trackInfo, this);
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

}
