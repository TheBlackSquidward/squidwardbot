package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioTrack;

public class SpotifyAudioTrack extends MirroringAudioTrack {

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkURL, SpotifySourceManager sourceManager) {
        super(trackInfo, isrc, artworkURL, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new SpotifyAudioTrack(trackInfo, isrc, artworkUrl, (SpotifySourceManager) sourceManager);
    }

}
