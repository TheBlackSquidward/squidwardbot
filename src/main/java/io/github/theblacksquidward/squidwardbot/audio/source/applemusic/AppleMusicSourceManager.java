package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppleMusicSourceManager implements AudioSourceManager, HttpConfigurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppleMusicSourceManager.class);
    private static final Pattern APPLE_MUSIC_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?music\\.apple\\.com/(?<countrycode>[a-zA-Z]{2}/)?(?<type>album|playlist|artist)(/[a-zA-Z0-9\\-]+)?/(?<identifier>[a-zA-Z0-9.]+)(\\?i=(?<identifier2>\\d+))?");

    private final AudioPlayerManager audioPlayerManager;
    private final HttpInterfaceManager httpInterfaceManager;

    public AppleMusicSourceManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
        this.httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    @Override
    public String getSourceName() {
        return "applemusic";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager audioPlayerManager, AudioReference audioReference) {
        Matcher matcher = APPLE_MUSIC_URL_PATTERN.matcher(audioReference.identifier);
        if(!matcher.find()) {
            return null;
        }
        String identifier = matcher.group("identifier");
        String type = matcher.group("type");
        AudioItem result;
        switch (type) {
            case "album" -> {
                String identifier2 = matcher.group("identifier2");
                if(identifier2 == null || identifier2.isEmpty()){
                    result = this.getAlbum(identifier);
                }
                result = this.getTrack(identifier2);
            }
            case "playlist" -> result = this.getPlaylist(identifier);
            case "artist" ->  result = this.getArtist(identifier);
            default -> throw new IllegalArgumentException();
        }
        return result;
    }

    private AudioItem getAlbum(String identifier) {
        return null;
    }

    private AudioItem getTrack(String identifier) {
        return null;
    }

    private AudioItem getPlaylist(String identifier) {
        return null;
    }

    private AudioItem getArtist(String identifier) {
        return null;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack audioTrack) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack audioTrack, DataOutput dataOutput) throws IOException {
        AppleMusicTrack appleMusicTrack = (AppleMusicTrack) audioTrack;
        DataFormatTools.writeNullableText(dataOutput, appleMusicTrack.getISRC());
        DataFormatTools.writeNullableText(dataOutput, appleMusicTrack.getArtworkUrl());
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo audioTrackInfo, DataInput dataInput) throws IOException {
        return new AppleMusicTrack(audioTrackInfo, DataFormatTools.readNullableText(dataInput), DataFormatTools.readNullableText(dataInput), this);
    }

    @Override
    public void shutdown() {
        ExceptionTools.closeWithWarnings(httpInterfaceManager);
    }

    @Override
    public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
        httpInterfaceManager.configureRequests(configurator);
    }

    @Override
    public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
        httpInterfaceManager.configureBuilder(configurator);
    }

}
