package io.github.theblacksquidward.squidwardbot.audio.source.mirror;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class MirroringAudioTrack extends DelegatedAudioTrack {

    private static final Logger LOGGER = LoggerFactory.getLogger(MirroringAudioTrack.class);

    protected final MirroringAudioSourceManager sourceManager;

    public MirroringAudioTrack(AudioTrackInfo trackInfo, MirroringAudioSourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        AudioItem track = sourceManager.getResolver().apply(this);

        if (track instanceof AudioPlaylist) {
            track = ((AudioPlaylist) track).getTracks().get(0);
        }
        if (track instanceof InternalAudioTrack) {
            processDelegate((InternalAudioTrack) track, executor);
            return;
        }
        throw new FriendlyException("No matching track found", FriendlyException.Severity.COMMON, new TrackNotFoundException());
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return sourceManager;
    }

    public AudioItem loadItem(String query) {
        CompletableFuture<AudioItem> completableFuture = new CompletableFuture<>();
        sourceManager.getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                LOGGER.debug("Track loaded: " + track.getIdentifier());
                completableFuture.complete(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                LOGGER.debug("Playlist loaded: " + playlist.getAudioPlaylistInfo().getName());
                completableFuture.complete(playlist);
            }

            @Override
            public void noMatches() {
                LOGGER.debug("No matches found for: " + query);
                completableFuture.complete(AudioReference.NO_TRACK);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                LOGGER.debug("Failed to load: " + query);
                completableFuture.completeExceptionally(exception);
            }

        });
        return completableFuture.join();
    }

}
