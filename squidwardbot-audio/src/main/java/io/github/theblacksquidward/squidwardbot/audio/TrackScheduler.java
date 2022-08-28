package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.filter.*;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
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
    private static final float[] BASS_BOOST = {0.15f, 0.14f, 0.13f, 0.14f, 0.05f, 0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.09f, 0.1f};

    private final AudioPlayer audioPlayer;
    private BlockingDeque<AudioTrack> queue;

    private boolean isRepeating = false;

    private boolean isNightcore = false;
    private double nightcoreSpeed = 1.0;

    private boolean isBassBoosted = false;
    private float bassBoostMultiplier = 0.0f;

    private volatile boolean shouldRebuildFilters = false;
    private volatile List<AudioFilter> audioFilterChain;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void toggleRepeating() {
        this.isRepeating = !this.isRepeating;
    }

    public boolean isNightcore() {
        return isNightcore;
    }

    public void enableNightcore() {
        this.isNightcore = true;
        updateFilters();
    }

    public void disableNightcore() {
        this.isNightcore = false;
        this.nightcoreSpeed = 1.0f;
        updateFilters();
    }

    public void setNightcoreSpeed(double speed) {
        nightcoreSpeed = speed;
        updateFilters();
    }

    public double getNightcoreSpeed() {
        return nightcoreSpeed;
    }

    public boolean isBassBoosted() {
        return isBassBoosted;
    }

    public void enableBassBoost() {
        this.isBassBoosted = true;
        updateFilters();
    }

    public void disableBassBoost() {
        this.isBassBoosted = false;
        this.bassBoostMultiplier = 0.0f;
        updateFilters();
    }

    public void setBassBoostMultiplier(int percentage) {
        bassBoostMultiplier = (float) percentage / 100.00f;
        updateFilters();
    }

    public int getBassBoostPercentage() {
        return (int) (bassBoostMultiplier * 100);
    }

    public void resetFilters() {
        this.isNightcore = false;
        this.nightcoreSpeed = 1.0f;
        this.isBassBoosted = false;
        this.bassBoostMultiplier = 0.0f;
        shouldRebuildFilters = false;
        audioPlayer.setFilterFactory(null);
    }

    public void updateFilters() {
        shouldRebuildFilters = true;
        audioPlayer.setFilterFactory(this::getOrRebuildFilters);
        audioPlayer.setFrameBufferDuration(500);
    }

    private List<AudioFilter> getOrRebuildFilters(AudioTrack audioTrack, AudioDataFormat audioDataFormat, UniversalPcmAudioFilter downstreamAudioFilter) {
        if (shouldRebuildFilters) {
            audioFilterChain = buildAudioFilterChain(audioTrack, audioDataFormat, downstreamAudioFilter);
            shouldRebuildFilters = false;
        }
        return audioFilterChain;
    }

    private List<AudioFilter> buildAudioFilterChain(AudioTrack audioTrack, AudioDataFormat audioDataFormat, UniversalPcmAudioFilter downstreamAudioFilter) {
        List<AudioFilter> filterList = new ArrayList<>();
        FloatPcmAudioFilter filter = downstreamAudioFilter;

        if(isNightcore) {
            ResamplingPcmAudioFilter resamplingPcmAudioFilter = new ResamplingPcmAudioFilter(
                    AudioManager.getAudioConfiguration(),
                    audioDataFormat.channelCount,
                    filter,
                    audioDataFormat.sampleRate,
                    (int) (audioDataFormat.sampleRate / nightcoreSpeed)
            );
            filterList.add(resamplingPcmAudioFilter);
            filter = resamplingPcmAudioFilter;
        }
        if(isBassBoosted) {
            Equalizer equalizer = new Equalizer(audioDataFormat.channelCount, filter);
            for (int i = 0; i < BASS_BOOST.length; i++) {
                equalizer.setGain(i, BASS_BOOST[i] * bassBoostMultiplier);
            }
            filter = equalizer;
            filterList.add(equalizer);
        }
        Collections.reverse(filterList);
        return filterList;
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

    public int getPositionInQueue(AudioTrack audioTrack) {
        int counter = 0;
        for (AudioTrack inQueue : queue) {
            if(audioTrack == inQueue) {
                return counter;
            }
            counter++;
        }
        return counter;
    }

    public void nextTrack() {
        if(!queue.isEmpty()) {
            this.audioPlayer.startTrack(this.queue.poll(), false);
        } else {
            this.audioPlayer.startTrack(null, false);
        }
    }

    public void forceQueueTrack(AudioTrack audioTrack) {
        addTrackAtHead(audioTrack);
        nextTrack();
    }

    public void forceQueueTracks(List<AudioTrack> audioTracks) {
        Collections.reverse(audioTracks);
        audioTracks.forEach(this::addTrackAtHead);
        nextTrack();
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
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        updateFilters();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack audioTrack, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext) {
            if(isRepeating) {
                addTrackAtHead(audioTrack.makeClone());
            }
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOGGER.error("Exception caught that caused the audio to halt or not to start. {}", exception.getStackTrace());
    }

}
