package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.delegating.DelegatingAudioTrack;

public class SpotifyAudioTrack extends DelegatingAudioTrack {

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkUrl, SpotifySourceManager sourceManager) {
        super(trackInfo, isrc, artworkUrl, sourceManager);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new SpotifyAudioTrack(trackInfo, isrc, artworkUrl, (SpotifySourceManager) sourceManager);
    }

}
