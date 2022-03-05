package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

public class AppleMusicTrack extends DelegatedAudioTrack {

    private final String isrc;
    private final String artworkUrl;
    private final AppleMusicSourceManager sourceManager;

    public AppleMusicTrack(AudioTrackInfo trackInfo, String isrc, String artworkUrl, AppleMusicSourceManager sourceManager) {
        super(trackInfo);
        this.isrc = isrc;
        this.artworkUrl = artworkUrl;
        this.sourceManager = sourceManager;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getISRC() {
        return isrc;
    }

    @Override
    public AppleMusicSourceManager getSourceManager() {
        return sourceManager;
    }

    @Override
    public void process(LocalAudioTrackExecutor localAudioTrackExecutor) throws Exception {

    }

}
