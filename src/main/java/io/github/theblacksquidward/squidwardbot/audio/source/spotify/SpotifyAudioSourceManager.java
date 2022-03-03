package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyAudioSourceManager implements AudioSourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioSourceManager.class);
    private static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist|artist)/(?<identifier>[a-zA-Z0-9-_]+)");

    private final SpotifyApi spotifyApi;

    public SpotifyAudioSourceManager(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    @Override
    public String getSourceName() {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        Matcher matcher = SPOTIFY_URL_PATTERN.matcher(reference.identifier);
        if(!matcher.find()) {
            return null;
        }
        String identifier = matcher.group("identifier");
        String type = matcher.group("type");
        return switch (type) {
            case "track" -> this.getTrack(identifier);
            case "playlist" -> this.getPlaylist(identifier);
            case "album" -> this.getAlbum(identifier);
            case "artist" -> this.getArtist(identifier);
        };
    }

    private AudioItem getTrack(String identifier) {
        return null;
    }

    private AudioItem getPlaylist(String identifier) {
        return null;
    }

    private AudioItem getAlbum(String identifier) {
        return null;
    }

    private AudioItem getArtist(String identifier) {
        return null;
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
