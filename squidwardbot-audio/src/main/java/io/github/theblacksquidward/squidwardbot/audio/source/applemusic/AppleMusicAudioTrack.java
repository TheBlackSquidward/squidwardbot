package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioTrack;

public class AppleMusicAudioTrack extends MirroringAudioTrack {

    public AppleMusicAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkUrl, AppleMusicSourceManager sourceManager) {
        super(trackInfo, isrc, artworkUrl, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new AppleMusicAudioTrack(trackInfo, isrc, artworkUrl, (AppleMusicSourceManager) sourceManager);
    }

}
