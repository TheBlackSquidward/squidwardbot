package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class SpotifyAudioTrack extends DelegatedAudioTrack {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioTrack.class);

    private final SpotifyAudioSourceManager sourceManager;

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, SpotifyAudioSourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
    }

    @Override
    public SpotifyAudioSourceManager getSourceManager() {
        return sourceManager;
    }

    private String getTrackTitle() {
        String result = this.trackInfo.title;
        if(!this.trackInfo.author.equals("unknown")){
            result += " " + this.trackInfo.author;
        }
        return result;
    }

    @Override
    public void process(LocalAudioTrackExecutor localAudioTrackExecutor) throws Exception {
        AudioItem track;
        String provider = "";

        if(trackInfo.isrc != null) {
            provider = "ytsearch:\"" + trackInfo.isrc + "\"";
        }
        track = loadItem(provider);
        if(track == null) {
            provider = "ytsearch:" + getTrackTitle();
        }
        track = loadItem(provider);

        if(track instanceof AudioPlaylist){
            track = ((AudioPlaylist) track).getTracks().get(0);
        }
        if(track instanceof InternalAudioTrack){
            processDelegate((InternalAudioTrack) track, localAudioTrackExecutor);
        }
    }

    private AudioItem loadItem(String provider) {
        var cf = new CompletableFuture<AudioItem>();
        this.sourceManager.getAudioPlayerManager().loadItem(provider, new AudioLoadResultHandler(){
            @Override
            public void trackLoaded(AudioTrack track){
                cf.complete(track);
            }
            @Override
            public void playlistLoaded(AudioPlaylist playlist){
                cf.complete(playlist);
            }
            @Override
            public void noMatches(){
                cf.complete(null);
            }
            @Override
            public void loadFailed(FriendlyException exception){
                cf.completeExceptionally(exception);
            }
        });
        return cf.join();
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new SpotifyAudioTrack(trackInfo, sourceManager);
    }

}
