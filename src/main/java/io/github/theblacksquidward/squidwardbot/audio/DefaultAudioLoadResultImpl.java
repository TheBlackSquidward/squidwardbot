package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class DefaultAudioLoadResultImpl extends BaseAudioLoadResultImpl {

    public DefaultAudioLoadResultImpl(TrackScheduler trackScheduler) {
        super(trackScheduler);
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        trackScheduler.queue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        audioPlaylist.getTracks().forEach(trackScheduler::queue);
    }

}
