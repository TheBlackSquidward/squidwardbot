package io.github.theblacksquidward.squidwardbot.audio.source;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifyAudioSourceManager;

import java.io.DataInput;
import java.io.DataOutput;

public class GeneralSearchSourceManager implements AudioSourceManager {

    @Override
    public String getSourceName() {
        return "general_search";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager audioPlayerManager, AudioReference audioReference) {
        String identifier = audioReference.identifier;
        AudioItem result = audioPlayerManager.source(SpotifyAudioSourceManager.class).loadItem(audioPlayerManager, new AudioReference("spsearch:" + identifier, null));
        if(result != null) {
            return result;
        }
        result = audioPlayerManager.source(SoundCloudAudioSourceManager.class).loadItem(audioPlayerManager, new AudioReference("scsearch:" + identifier, null));
        return result;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        // Nothing special to decode
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return null;
    }

    @Override
    public void shutdown() {

    }

}
