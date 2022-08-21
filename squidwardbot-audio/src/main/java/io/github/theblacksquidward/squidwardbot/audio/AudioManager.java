package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import genius.SongSearch;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifyAudioSourceManager;
import io.github.theblacksquidward.squidwardbot.core.constants.Constants;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AudioManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioManager.class);

    private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Long, GuildAudioManager> GUILD_AUDIO_MANAGERS = new HashMap<>();

    static {
        //TODO
        AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        AUDIO_PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        AUDIO_PLAYER_MANAGER.registerSourceManager(new SpotifyAudioSourceManager(AUDIO_PLAYER_MANAGER));
    }

    public static GuildAudioManager getOrCreate(Guild guild) {
        GUILD_AUDIO_MANAGERS.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildAudioManager guildAudioManager = new GuildAudioManager(AUDIO_PLAYER_MANAGER.createPlayer());
            guild.getAudioManager().setSendingHandler(guildAudioManager.getAudioPlayerSendHandler());
            return guildAudioManager;
        });
        return GUILD_AUDIO_MANAGERS.get(guild.getIdLong());
    }

    public static void repeatTrack(Guild guild) {
        getOrCreate(guild).getTrackScheduler().addTrackAtHead(getCurrentlyPlayingTrack(guild).makeClone());
    }

    public static boolean isQueueEmpty(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isQueueEmpty();
    }

    public static int getQueueSize(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getQueueSize();
    }

    public static void shuffleQueue(Guild guild) {
        getOrCreate(guild).getTrackScheduler().shuffleQueue();
    }

    public static List<AudioTrack> getQueuedTracks(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getQueuedTracks();
    }

    public static void clearQueue(Guild guild) {
        getOrCreate(guild).getTrackScheduler().clearQueue();
    }

    public static AudioTrack getCurrentlyPlayingTrack(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getCurrentlyPlayingTrack();
    }

    public static AudioTrack skipTrack(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().skip();
    }

    public static boolean isPlayingTrack(Guild guild) {
        return getOrCreate(guild).getAudioPlayer().getPlayingTrack() != null;
    }

    public static void pauseTrack(Guild guild) {
        getOrCreate(guild).getAudioPlayer().setPaused(true);
    }

    public static void resumeTrack(Guild guild) {
        getOrCreate(guild).getAudioPlayer().setPaused(false);
    }

    public static boolean isPaused(Guild guild) {
        return getOrCreate(guild).getAudioPlayer().isPaused();
    }

    public static int getVolume(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getVolume();
    }

    public static void setVolume(Guild guild, int volume) {
        getOrCreate(guild).getTrackScheduler().setVolume(volume);
    }

    public static boolean isBassBoosted(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isBassBoosted();
    }

    public static void enableBassBoost(Guild guild) {
        getOrCreate(guild).getTrackScheduler().enableBassBoost();
    }

    public static void disableBassBoost(Guild guild) {
        getOrCreate(guild).getTrackScheduler().disableBassBoost();
    }

    public static void setBassBoostLevel(Guild guild, int percentage) {
        getOrCreate(guild).getTrackScheduler().setBassBoostLevel(percentage);
    }

    public static SongSearch getLyrics(Guild guild) throws IOException {
        return Constants.GENIUS_LYRICS_API.search(getCurrentlyPlayingTrack(guild).getInfo().title + "" + getCurrentlyPlayingTrack(guild).getInfo().author);
    }

    public static void loadAndPlay(Guild guild, String identifier) {
        final GuildAudioManager guildAudioManager = getOrCreate(guild);
        loadAndPlay(guild, identifier, new BaseAudioLoadResultImpl(guildAudioManager.getTrackScheduler()));
    }

    public static void loadAndPlay(Guild guild, String identifier, AudioLoadResultHandler audioLoadResult) {
        final GuildAudioManager guildAudioManager = getOrCreate(guild);
        AUDIO_PLAYER_MANAGER.loadItemOrdered(guildAudioManager, identifier, audioLoadResult);
    }

    //TODO should this be here or a helper else where
    public static String formatTrackTimeDuration(long timeInMilliseconds) {
        long hours = timeInMilliseconds / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMilliseconds / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMilliseconds % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
