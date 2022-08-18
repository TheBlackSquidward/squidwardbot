package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

public class AppleMusicTrack extends DelegatedAudioTrack {

    private final AppleMusicSourceManager sourceManager;

    public AppleMusicTrack(AudioTrackInfo trackInfo, AppleMusicSourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
    }

    @Override
    public AppleMusicSourceManager getSourceManager() {
        return sourceManager;
    }

    @Override
    public void process(LocalAudioTrackExecutor localAudioTrackExecutor) throws Exception {

    }

}
