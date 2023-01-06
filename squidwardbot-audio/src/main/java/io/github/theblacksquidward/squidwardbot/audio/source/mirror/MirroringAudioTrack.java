package io.github.theblacksquidward.squidwardbot.audio.source.mirror;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import io.github.theblacksquidward.squidwardbot.audio.source.applemusic.AppleMusicSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifySourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioSourceManager.ISRC_PATTERN;
import static io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioSourceManager.QUERY_PATTERN;

public abstract class MirroringAudioTrack extends DelegatedAudioTrack {

    private static final Logger log = LoggerFactory.getLogger(MirroringAudioTrack.class);

    protected final String isrc;
    protected final String artworkUrl;
    protected final MirroringAudioSourceManager sourceManager;

    public MirroringAudioTrack(AudioTrackInfo trackInfo, String isrc, String artworkUrl, MirroringAudioSourceManager sourceManager) {
        super(trackInfo);
        this.isrc = isrc;
        this.artworkUrl = artworkUrl;
        this.sourceManager = sourceManager;
    }

    public String getISRC() {
        return this.isrc;
    }

    public String getArtworkUrl() {
        return this.artworkUrl;
    }

    private String getTrackTitle() {
        var query = this.trackInfo.title;
        if (!this.trackInfo.author.equals("unknown")) {
            query += " " + this.trackInfo.author;
        }
        return query;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        AudioItem track = null;

        for (var provider : this.sourceManager.getProviders()) {
            if (provider.startsWith(SpotifySourceManager.SEARCH_PREFIX)) {
                log.warn("Can not use spotify search as search provider!");
                continue;
            }

            if (provider.startsWith(AppleMusicSourceManager.SEARCH_PREFIX)) {
                log.warn("Can not use apple music search as search provider!");
                continue;
            }

            if (provider.contains(ISRC_PATTERN)) {
                if (this.isrc != null) {
                    provider = provider.replace(ISRC_PATTERN, this.isrc);
                } else {
                    log.debug("Ignoring identifier \"" + provider + "\" because this track does not have an ISRC!");
                    continue;
                }
            }

            provider = provider.replace(QUERY_PATTERN, getTrackTitle());
            track = loadItem(provider);
            if (track != AudioReference.NO_TRACK) {
                break;
            }
        }

        if (track instanceof AudioPlaylist) {
            track = ((AudioPlaylist) track).getTracks().get(0);
        }
        if (track instanceof InternalAudioTrack) {
            processDelegate((InternalAudioTrack) track, executor);
            return;
        }
        throw new FriendlyException("No matching track found", FriendlyException.Severity.COMMON, new TrackNotFoundException());
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return this.sourceManager;
    }

    private AudioItem loadItem(String query) {
        var cf = new CompletableFuture<AudioItem>();
        this.sourceManager.getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                log.debug("Track loaded: " + track.getIdentifier());
                cf.complete(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                log.debug("Playlist loaded: " + playlist.getName());
                cf.complete(playlist);
            }

            @Override
            public void noMatches() {
                log.debug("No matches found for: " + query);
                cf.complete(AudioReference.NO_TRACK);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.debug("Failed to load: " + query);
                cf.completeExceptionally(exception);
            }
        });
        return cf.join();
    }

}
