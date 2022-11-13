package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.delegating.DelegatingAudioTrack;

public class AppleMusicAudioTrack extends DelegatingAudioTrack {

    public AppleMusicAudioTrack(AudioTrackInfo trackInfo, AppleMusicSourceManager sourceManager) {
        super(trackInfo, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new AppleMusicAudioTrack(trackInfo, (AppleMusicSourceManager) sourceManager);
    }

}
