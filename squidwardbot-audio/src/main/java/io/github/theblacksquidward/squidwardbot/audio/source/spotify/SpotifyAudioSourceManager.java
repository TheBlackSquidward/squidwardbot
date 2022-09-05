package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpotifyAudioSourceManager implements AudioSourceManager {

    public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist|artist)/(?<identifier>[a-zA-Z0-9-_]+)");
    public static final String SEARCH_PREFIX = "spsearch:";
    public static final int PLAYLIST_MAX_PAGE_ITEMS = 100;
    public static final int ALBUM_MAX_PAGE_ITEMS = 50;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioSourceManager.class);

    private final SpotifyApi spotifyApi;
    private final ClientCredentialsRequest spotifyClientCredentialsRequest;
    private final Thread spotifyApiRefreshThread;
    private final AudioPlayerManager audioPlayerManager;


    public SpotifyAudioSourceManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(SquidwardBot.getInstance().getSpotifyClientId())
                .setClientSecret(SquidwardBot.getInstance().getSpotifyClientSecret())
                .build();
        this.spotifyClientCredentialsRequest = spotifyApi.clientCredentials().build();
        spotifyApiRefreshThread = new Thread(() -> {
            try {
                while(true) {
                    try {
                        ClientCredentials clientCredentials = this.spotifyClientCredentialsRequest.execute();
                        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
                        Thread.sleep((clientCredentials.getExpiresIn() - 10) * 1000L);
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        LOGGER.error("Failed to refresh the Spotify access token. Attempting to refresh in 1 minute... ", e);
                        Thread.sleep(60 * 1000L);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to refresh the Spotify access token.", e);
            }
        });
        spotifyApiRefreshThread.setDaemon(true);
        spotifyApiRefreshThread.start();
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
            Matcher matcher = SPOTIFY_URL_PATTERN.matcher(audioReference.identifier);
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
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    private AudioItem getFirstSearchResultAsTrack(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        List<AudioTrack> result = getSearchResults(identifier);
        if(result.isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return result.get(0);
    }

    private AudioItem getAllSearchResultsAsPlaylist(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        List<AudioTrack> result = getSearchResults(identifier);
        if(result.isEmpty()) {
            return AudioReference.NO_TRACK;
        }
        return new BasicAudioPlaylist("Search results for: " + identifier, result, null, true);
    }

    private List<AudioTrack> getSearchResults(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Paging<Track> searchResult = this.spotifyApi.searchTracks(identifier).build().execute();
        if(searchResult.getItems().length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(searchResult.getItems())
                .map((track) -> SpotifyAudioTrack.createSpotifyTrack(track, this))
                .collect(Collectors.toList());
    }

    private AudioItem getTrack(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        return SpotifyAudioTrack.createSpotifyTrack(this.spotifyApi.getTrack(identifier).build().execute(), this);
    }

    private AudioItem getPlaylist(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Playlist playlist = this.spotifyApi.getPlaylist(identifier).build().execute();
        Paging<PlaylistTrack> playlistTracks = this.spotifyApi.getPlaylistsItems(identifier).limit(PLAYLIST_MAX_PAGE_ITEMS).build().execute();
        List<AudioTrack> tracks = new ArrayList<>();

        Arrays.stream(playlistTracks.getItems()).forEach((track) -> tracks.add(SpotifyAudioTrack.createSpotifyTrack((Track) track.getTrack(), this)));
        return new BasicAudioPlaylist(playlist.getName(), tracks, null, false);
    }

    private AudioItem getAlbum(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Album album = this.spotifyApi.getAlbum(identifier).build().execute();
        Paging<TrackSimplified> albumTracks = this.spotifyApi.getAlbumsTracks(identifier).limit(ALBUM_MAX_PAGE_ITEMS).build().execute();
        List<AudioTrack> tracks = new ArrayList<>();

        Arrays.stream(albumTracks.getItems()).forEach((trackSimplified) -> tracks.add(SpotifyAudioTrack.createSpotifyTrack(trackSimplified, album, this)));
        return new BasicAudioPlaylist(album.getName(), tracks, null, false);
    }

    private AudioItem getArtist(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Artist artist = this.spotifyApi.getArtist(identifier).build().execute();
        Track[] artistTracks = this.spotifyApi.getArtistsTopTracks(identifier, CountryCode.GB).build().execute();
        List<AudioTrack> tracks = new ArrayList<>();

        Arrays.stream(artistTracks).toList().forEach((track) -> tracks.add(SpotifyAudioTrack.createSpotifyTrack(track, this)));
        return new BasicAudioPlaylist(artist.getName() + "'s Top Tracks", tracks, null,  false);
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

    }

}
