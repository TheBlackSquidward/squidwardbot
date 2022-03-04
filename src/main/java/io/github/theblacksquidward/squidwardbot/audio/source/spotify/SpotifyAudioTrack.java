package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.util.concurrent.CompletableFuture;

public class SpotifyAudioTrack extends DelegatedAudioTrack {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAudioTrack.class);
    private static final String test = "";

    private final SpotifyAudioSourceManager sourceManager;
    private final String isrc;
    private final String artworkUrl;


    private SpotifyAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkUrl, SpotifyAudioSourceManager sourceManager) {
        super(trackInfo);
        this.sourceManager = sourceManager;
        this.isrc = isrc;
        this.artworkUrl = artworkUrl;
    }

    public String getISRC() {
        return isrc;
    }

    @Nullable
    public String getArtworkUrl() {
        return artworkUrl;
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

    //TODO clean
    @Override
    public void process(LocalAudioTrackExecutor localAudioTrackExecutor) throws Exception {
        AudioItem track = null;
        String provider = "";

        if(this.isrc != null) {
            provider = "ytsearch:\"" + isrc + "\"";
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

    public static SpotifyAudioTrack createSpotifyTrack(TrackSimplified track, Album album, SpotifyAudioSourceManager sourceManager) {
        return createSpotifyTrack(track.getName(), track.getId(), null, album.getArtists(), album.getImages(), track.getDurationMs(), sourceManager);
    }

    public static SpotifyAudioTrack createSpotifyTrack(String title, String identifier, String isrc, ArtistSimplified[] artists, Image[] images, Integer trackDuration, SpotifyAudioSourceManager sourceManager) {
        return new SpotifyAudioTrack(new AudioTrackInfo(title,
                artists.length == 0 ? "unknown" : artists[0].getName(),
                trackDuration.longValue(),
                identifier,
                false,
                "https://open.spotify.com/track/" + identifier), isrc, images.length == 0 ? null : images[0].getUrl(), sourceManager);
    }

    public static SpotifyAudioTrack createSpotifyTrack(Track track, SpotifyAudioSourceManager sourceManager) {
        return createSpotifyTrack(track.getName(), track.getId(), track.getExternalIds().getExternalIds().getOrDefault("isrc", null), track.getArtists(), track.getAlbum().getImages(), track.getDurationMs(), sourceManager);
    }

}
