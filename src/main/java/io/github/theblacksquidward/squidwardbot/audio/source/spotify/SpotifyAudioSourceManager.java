package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyAudioSourceManager implements AudioSourceManager {

    public static final int PLAYLIST_MAX_PAGE_ITEMS = 100;
    public static final int ALBUM_MAX_PAGE_ITEMS = 50;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioSourceManager.class);
    private static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist|artist)/(?<identifier>[a-zA-Z0-9-_]+)");

    private final SpotifyApi spotifyApi;
    private final AudioPlayerManager audioPlayerManager;

    public SpotifyAudioSourceManager(SpotifyApi spotifyApi, AudioPlayerManager audioPlayerManager) {
        this.spotifyApi = spotifyApi;
        this.audioPlayerManager = audioPlayerManager;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    @Override
    public String getSourceName() {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        try {
            Matcher matcher = SPOTIFY_URL_PATTERN.matcher(reference.identifier);
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
                //TODO idk if i want this to be like that.
                default -> throw new IllegalArgumentException();
            };
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
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
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        throw new UnsupportedOperationException("This track can not be encoded.");
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        throw new UnsupportedOperationException("This track can not be decoded.");
    }

    @Override
    public void shutdown() {

    }

}
