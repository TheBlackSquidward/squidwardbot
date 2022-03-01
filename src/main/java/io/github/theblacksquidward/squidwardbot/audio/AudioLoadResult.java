package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioLoadResult implements AudioLoadResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioLoadResult.class);

    private final TrackScheduler trackScheduler;

    public AudioLoadResult(TrackScheduler trackScheduler) {
        this.trackScheduler = trackScheduler;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        trackScheduler.queue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        audioPlaylist.getTracks().forEach(this::trackLoaded);
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException e) {
        LOGGER.error("Caught an exception when trying to load an audio result. {}", e.getStackTrace());
    }

}
