package io.github.theblacksquidward.squidwardbot.audio.source.mirror;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import io.github.theblacksquidward.squidwardbot.audio.source.applemusic.AppleMusicSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifySourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioSourceManager.ISRC_PATTERN;
import static io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioSourceManager.QUERY_PATTERN;

//TODO cleanup
public class DefaultMirroringAudioTrackResolver implements MirroringAudioTrackResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMirroringAudioTrackResolver.class);

    private String[] providers = {
            "ytsearch:\"" + ISRC_PATTERN + "\"",
            "ytsearch:" + QUERY_PATTERN
    };

    public DefaultMirroringAudioTrackResolver(String[] providers) {
        if (providers != null && providers.length > 0) {
            this.providers = providers;
        }
    }

    @Override
    public AudioItem apply(MirroringAudioTrack mirroringAudioTrack) {
        AudioItem track = AudioReference.NO_TRACK;
        for (String provider : providers) {
            if (provider.startsWith(SpotifySourceManager.SEARCH_PREFIX)) {
                LOGGER.warn("Can not use spotify search as search provider!");
                continue;
            }

            if (provider.startsWith(AppleMusicSourceManager.SEARCH_PREFIX)) {
                LOGGER.warn("Can not use apple music search as search provider!");
                continue;
            }

            if (provider.contains(ISRC_PATTERN)) {
                if (mirroringAudioTrack.getInfo().isrc != null && !mirroringAudioTrack.getInfo().isrc.isEmpty()) {
                    provider = provider.replace(ISRC_PATTERN, mirroringAudioTrack.getInfo().isrc);
                } else {
                    LOGGER.debug("Ignoring identifier \"{}\" because this track does not have an ISRC!", provider);
                    continue;
                }
            }

            provider = provider.replace(QUERY_PATTERN, getTrackTitle(mirroringAudioTrack));
            track = mirroringAudioTrack.loadItem(provider);
            if (track != AudioReference.NO_TRACK) {
                break;
            }
        }

        return track;
    }

    public String getTrackTitle(MirroringAudioTrack mirroringAudioTrack) {
        var query = mirroringAudioTrack.getInfo().title;
        if (!mirroringAudioTrack.getInfo().author.equals("unknown")) {
            query += " " + mirroringAudioTrack.getInfo().author;
        }
        return query;
    }

}
