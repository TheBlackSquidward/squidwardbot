package io.github.theblacksquidward.squidwardbot.audio.source.delegating;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.DataOutput;
import java.io.IOException;

public abstract class DelegatingSourceManager implements AudioSourceManager {

    public static final String ISRC_PATTERN = "%ISRC%";
    public static final String QUERY_PATTERN = "%QUERY%";

    private final String[] PROVIDERS = {
            "ytsearch:\"" + ISRC_PATTERN + "\"",
            "ytsearch:" + QUERY_PATTERN
    };

    protected final AudioPlayerManager audioPlayerManager;

    protected DelegatingSourceManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return this.audioPlayerManager;
    }

    public String[] getProviders() {
        return PROVIDERS;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
        DelegatingAudioTrack delegatingAudioTrack = ((DelegatingAudioTrack) track);
        DataFormatTools.writeNullableText(output, delegatingAudioTrack.getIsrc());
        DataFormatTools.writeNullableText(output, delegatingAudioTrack.getArtworkUrl());
    }

}
