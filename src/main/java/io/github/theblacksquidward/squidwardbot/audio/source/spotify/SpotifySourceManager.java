package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class SpotifySourceManager implements AudioSourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifySourceManager.class);
    private static final String SPOTIFY_DOMAIN = "open.spotify.com";

    private final SpotifyApi spotifyApi;

    public SpotifySourceManager(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        try {
            URL url = new URL(reference.identifier);
            if(!Objects.equals(url.getHost(), SPOTIFY_DOMAIN)) {
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public String getSourceName() {
        return "spotify";
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
