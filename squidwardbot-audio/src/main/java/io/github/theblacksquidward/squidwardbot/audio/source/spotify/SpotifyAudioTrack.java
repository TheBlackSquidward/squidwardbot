package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import io.github.theblacksquidward.squidwardbot.audio.source.applemusic.AppleMusicSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class SpotifyAudioTrack extends DelegatedAudioTrack {

    private static final String ISRC_PATTERN = "%ISRC%";
    private static final String QUERY_PATTERN = "%QUERY%";

    private final String[] PROVIDERS = {
            "ytsearch:\"" + ISRC_PATTERN + "\"",
            "ytsearch:" + QUERY_PATTERN
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioTrack.class);

    private final SpotifySourceManager sourceManager;

    public SpotifyAudioTrack(AudioTrackInfo trackInfo, SpotifySourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
    }

    @Override
    public SpotifySourceManager getSourceManager() {
        return sourceManager;
    }

    private String getTrackTitle() {
        String result = this.trackInfo.title;
        if(!this.trackInfo.author.equals("unknown")){
            result += " " + this.trackInfo.author;
        }
        return result;
    }

    //TODO i wanna rewrite this if possible
    @Override
    public void process(LocalAudioTrackExecutor localAudioTrackExecutor) throws Exception {
        AudioItem audioItem = null;
        for(String provider : PROVIDERS) {
            if(provider.startsWith(SpotifySourceManager.SEARCH_PREFIX)) {
                LOGGER.warn("Can not use spotify search as search provider!");
                continue;
            }
            if(provider.startsWith(AppleMusicSourceManager.SEARCH_PREFIX)) {
                LOGGER.warn("Can not use apple music search as search provider!");
                continue;
            }
            if(provider.contains(ISRC_PATTERN)) {
                if(this.trackInfo.isrc != null) {
                    provider = provider.replace(ISRC_PATTERN, this.trackInfo.isrc);
                } else {
                    LOGGER.debug("Ignoring identifier \"" + provider + "\" because this track does not have an ISRC!");
                    continue;
                }
            }
            provider = provider.replace(QUERY_PATTERN, getTrackTitle());
            audioItem = loadItem(provider);
            if(audioItem != AudioReference.NO_TRACK){
                break;
            }
        }
        if(audioItem instanceof AudioPlaylist) {
            audioItem = ((AudioPlaylist) audioItem).getTracks().get(0);
        }
        if(audioItem instanceof InternalAudioTrack) {
            processDelegate((InternalAudioTrack) audioItem, localAudioTrackExecutor);
        }
    }

    private AudioItem loadItem(String provider) {
        CompletableFuture<AudioItem> cf = new CompletableFuture<>();
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
                cf.complete(AudioReference.NO_TRACK);
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
