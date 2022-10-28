package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.delegating.DelegatingAudioTrack;

public class AppleMusicAudioTrack extends DelegatingAudioTrack {

    public AppleMusicAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkUrl, AppleMusicSourceManager sourceManager) {
        super(trackInfo, isrc, artworkUrl, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new AppleMusicAudioTrack(trackInfo, isrc, artworkUrl, (AppleMusicSourceManager) sourceManager);
    }

}
