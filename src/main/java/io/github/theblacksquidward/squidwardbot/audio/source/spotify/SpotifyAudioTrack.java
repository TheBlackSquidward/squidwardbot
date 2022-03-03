package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

public class SpotifyAudioTrack extends DelegatedAudioTrack {

    private final SpotifyAudioSourceManager sourceManager;
    private final String artworkUrl;

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, String artworkUrl, SpotifyAudioSourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
        this.artworkUrl = artworkUrl;
    }

    @Override
    public void process(LocalAudioTrackExecutor localAudioTrackExecutor) throws Exception {

    }

}
