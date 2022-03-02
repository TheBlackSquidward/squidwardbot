package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer audioPlayer;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if(!this.audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void queueTrack(AudioTrack audioTrack) {
    }

    public void clearQueue() {
        queue.clear();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public Set<AudioTrack> getQueuedTracks() {
        return new LinkedHashSet<>(queue);
    }

    public void nextTrack() {
        this.audioPlayer.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(!endReason.mayStartNext) {
            //TODO log that
        }
        nextTrack();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOGGER.error("Exception caught that caused the audio to halt or not to start. {}", exception.getStackTrace());
    }

}
