package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AppleMusicSourceManager implements AudioSourceManager {

    @Override
    public String getSourceName() {
        return "applemusic";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager audioPlayerManager, AudioReference audioReference) {
        return null;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack audioTrack) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack audioTrack, DataOutput dataOutput) throws IOException {

    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo audioTrackInfo, DataInput dataInput) throws IOException {
        return null;
    }

    @Override
    public void shutdown() {

    }

}
