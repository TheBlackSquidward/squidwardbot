package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.ResamplingPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
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

    private final float NIGHTCORE_TEMPO = 1.15f;
    private final float SLOWED_TEMPO = 0.85f;

    private final AudioPlayer audioPlayer;
    private BlockingDeque<AudioTrack> queue;

    private boolean isRepeating = false;
    private BlockingDeque<AudioTrack> repeatingQueue;

    private boolean isNightcore = false;
    private boolean isSlowed = false;

    private float tempo = 1.0f;

    private boolean isBassBoosted = false;
    private float bassBoostMultiplier = 0.0f;

    private volatile boolean shouldRebuildFilters = false;
    private volatile List<AudioFilter> audioFilterChain;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
        this.repeatingQueue = new LinkedBlockingDeque<>();
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void toggleRepeating() {
        this.isRepeating = !this.isRepeating;
        if (isRepeating) {
            queue.forEach(track -> repeatingQueue.offer(track));
            repeatingQueue.offer(getCurrentlyPlayingTrack().makeClone());
            queue.clear();
        } else {
            while (!repeatingQueue.isEmpty()) {
                queue.offer(repeatingQueue.poll());
            }
            queue.pollLast();
        }
    }

    public BlockingDeque<AudioTrack> getQueue() {
        return queue;
    }

    public BlockingDeque<AudioTrack> getRepeatingQueue() {
        return repeatingQueue;
    }

    public float getTempo() {
        return tempo;
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
        updateFilters();
    }

    public void resetTempo() {
        this.tempo = 1.0f;
        updateFilters();
    }

    public boolean isNightcore() {
        return isNightcore;
    }

    public boolean isSlowed() {
        return isSlowed;
    }

    public void enableSlowed() {
        this.isSlowed = true;
        setTempo(SLOWED_TEMPO);
    }

    public void disableSlowed() {
        this.isSlowed = false;
        resetTempo();
    }

    public void enableNightcore() {
        this.isNightcore = true;
        setTempo(NIGHTCORE_TEMPO);
    }

    public void disableNightcore() {
        this.isNightcore = false;
        resetTempo();
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
        this.tempo = 1.0f;
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

        if(isSlowed || isNightcore) {
            ResamplingPcmAudioFilter resamplingPcmAudioFilter = new ResamplingPcmAudioFilter(
                    AudioManager.getAudioConfiguration(),
                    audioDataFormat.channelCount,
                    filter,
                    audioDataFormat.sampleRate,
                    (int) (audioDataFormat.sampleRate / tempo)
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
            if (isRepeating) {
                this.repeatingQueue.offer(track);
            } else {
                this.queue.offer(track);
            }
        }
    }

    public void removeTrack(int position) {
        if (isRepeating) {
            List<AudioTrack> tracks = new ArrayList<>(repeatingQueue);
            tracks.remove(position - 1);
            repeatingQueue = new LinkedBlockingDeque<>(tracks);
        } else {
            List<AudioTrack> tracks = new ArrayList<>(queue);
            tracks.remove(position - 1);
            queue = new LinkedBlockingDeque<>(tracks);
        }
    }

    public void clearQueue() {
        queue.clear();
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
        if (isRepeating) {
            if(!repeatingQueue.isEmpty()) {
                final AudioTrack audioTrack = repeatingQueue.poll();
                this.audioPlayer.startTrack(audioTrack, false);
                repeatingQueue.offer(audioTrack.makeClone());
            } else {
                this.audioPlayer.startTrack(null, false);
            }
        } else {
            if(!queue.isEmpty()) {
                this.audioPlayer.startTrack(this.queue.poll(), false);
            } else {
                this.audioPlayer.startTrack(null, false);
            }
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
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOGGER.error("Exception caught that caused the audio to halt or not to start. {}", exception.getStackTrace());
    }

}
