package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final SpotifyAPIOLD spotifyAPI = SpotifyAPIOLD.createSpotifyAPI();
    private final AudioPlayerManager audioPlayerManager;

    public SpotifyAudioSourceManager(AudioPlayerManager audioPlayerManager) {
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
                default -> throw new IllegalArgumentException();
            };
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }

    private AudioItem getTrack(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        return SpotifyAudioTrack.createSpotifyTrack(this.spotifyAPI.getSpotifyAPI().getTrack(identifier).build().execute(), this);
    }

    private AudioItem getPlaylist(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Playlist playlist = this.spotifyAPI.getSpotifyAPI().getPlaylist(identifier).build().execute();
        Paging<PlaylistTrack> playlistTracks = this.spotifyAPI.getSpotifyAPI().getPlaylistsItems(identifier).limit(PLAYLIST_MAX_PAGE_ITEMS).build().execute();
        List<AudioTrack> tracks = new ArrayList<>();

        Arrays.stream(playlistTracks.getItems()).forEach((track) -> tracks.add(SpotifyAudioTrack.createSpotifyTrack((Track) track.getTrack(), this)));
        return new BasicAudioPlaylist(playlist.getName(), tracks, null, false);
    }

    private AudioItem getAlbum(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Album album = this.spotifyAPI.getSpotifyAPI().getAlbum(identifier).build().execute();
        Paging<TrackSimplified> albumTracks = this.spotifyAPI.getSpotifyAPI().getAlbumsTracks(identifier).limit(ALBUM_MAX_PAGE_ITEMS).build().execute();
        List<AudioTrack> tracks = new ArrayList<>();

        Arrays.stream(albumTracks.getItems()).forEach((trackSimplified) -> tracks.add(SpotifyAudioTrack.createSpotifyTrack(trackSimplified, album, this)));
        return new BasicAudioPlaylist(album.getName(), tracks, null, false);
    }

    private AudioItem getArtist(String identifier) throws IOException, ParseException, SpotifyWebApiException {
        Artist artist = this.spotifyAPI.getSpotifyAPI().getArtist(identifier).build().execute();
        Track[] artistTracks = this.spotifyAPI.getSpotifyAPI().getArtistsTopTracks(identifier, CountryCode.GB).build().execute();
        List<AudioTrack> tracks = new ArrayList<>();

        Arrays.stream(artistTracks).toList().forEach((track) -> tracks.add(SpotifyAudioTrack.createSpotifyTrack(track, this)));
        return new BasicAudioPlaylist(artist.getName() + "'s Top Tracks", tracks, null,  false);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        SpotifyAudioTrack spotifyAudioTrack = (SpotifyAudioTrack) track;
        DataFormatTools.writeNullableText(output, spotifyAudioTrack.getISRC());
        DataFormatTools.writeNullableText(output, spotifyAudioTrack.getArtworkUrl());
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        return new SpotifyAudioTrack(trackInfo, DataFormatTools.readNullableText(input), DataFormatTools.readNullableText(input), this);
    }

    @Override
    public void shutdown() {

    }

}
