package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer audioPlayer;
    private BlockingDeque<AudioTrack> queue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }

    public int getVolume() {
        return this.audioPlayer.getVolume();
    }

    public void setVolume(int volume) {
        volume = Math.max(0, volume);
        volume = Math.min(1000, volume);
        this.audioPlayer.setVolume(volume);
    }

    public void queueTrack(AudioTrack track) {
        if(!this.audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void clearQueue() {
        List<AudioTrack> newQueue = new ArrayList<>();
        queue.drainTo(newQueue);
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public void shuffleQueue() {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        Collections.shuffle(tracks);
        queue = new LinkedBlockingDeque<>(tracks);
    }

    public int getQueueSize() {
        return queue.size();
    }

    public List<AudioTrack> getQueuedTracks() {
        return new ArrayList<>(queue);
    }

    public void nextTrack() {
        if(!queue.isEmpty()) {
            this.audioPlayer.startTrack(this.queue.poll(), false);
        } else {
            this.audioPlayer.startTrack(null, false);
        }
    }

    public void forcePlayTrack(AudioTrack audioTrack) {
        addTrackAtHead(audioTrack);
        audioPlayer.stopTrack();
    }

    public void addTrackAtHead(AudioTrack audioTrack) {
        queue.offerFirst(audioTrack);
    }

    public AudioTrack skip() {
        final AudioTrack currentTrack = getCurrentlyPlayingTrack();
        nextTrack();
        return currentTrack;
    }

    public AudioTrack getCurrentlyPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack audioTrack, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOGGER.error("Exception caught that caused the audio to halt or not to start. {}", exception.getStackTrace());
    }

}
