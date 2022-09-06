package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import genius.SongSearch;
import io.github.theblacksquidward.squidwardbot.audio.source.GeneralSearchSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifyAudioSourceManager;
import io.github.theblacksquidward.squidwardbot.core.constants.Constants;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioManager.class);

    private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Long, GuildAudioManager> GUILD_AUDIO_MANAGERS = new HashMap<>();

    static {
        AUDIO_PLAYER_MANAGER.registerSourceManager(new SpotifyAudioSourceManager(AUDIO_PLAYER_MANAGER));
        AUDIO_PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        AUDIO_PLAYER_MANAGER.registerSourceManager(new GeneralSearchSourceManager());
        AUDIO_PLAYER_MANAGER.getConfiguration().setFilterHotSwapEnabled(true);
    }

    public static GuildAudioManager getOrCreate(Guild guild) {
        return GUILD_AUDIO_MANAGERS.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildAudioManager guildAudioManager = new GuildAudioManager(AUDIO_PLAYER_MANAGER.createPlayer());
            guild.getAudioManager().setSendingHandler(guildAudioManager.getAudioPlayerSendHandler());
            guild.getAudioManager().setSelfDeafened(true);
            return guildAudioManager;
        });
    }

    public static AudioConfiguration getAudioConfiguration() {
        return AUDIO_PLAYER_MANAGER.getConfiguration();
    }

    public static void requeueTrack(Guild guild) {
        getOrCreate(guild).getTrackScheduler().addTrackAtHead(getCurrentlyPlayingTrack(guild).makeClone());
    }

    public static boolean isRepeating(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isRepeating();
    }

    public static void toggleRepeating(Guild guild) {
        getOrCreate(guild).getTrackScheduler().toggleRepeating();
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

    public static int getPositionInQueue(Guild guild, AudioTrack audioTrack) {
        return getOrCreate(guild).getTrackScheduler().getPositionInQueue(audioTrack);
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

    public static void setBassBoostMultiplier(Guild guild, int percentage) {
        getOrCreate(guild).getTrackScheduler().setBassBoostMultiplier(percentage);
    }

    public static int getBassBoostPercentage(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getBassBoostPercentage();
    }

    public static boolean isNightcore(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isNightcore();
    }

    public static void enableNightcore(Guild guild) {
        getOrCreate(guild).getTrackScheduler().enableNightcore();
    }

    public static void disableNightcore(Guild guild) {
        getOrCreate(guild).getTrackScheduler().disableNightcore();
    }

    public static void setNightcoreSpeed(Guild guild, double speed) {
        getOrCreate(guild).getTrackScheduler().setNightcoreSpeed(speed);
    }

    public static double getNightcoreSpeed(Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getNightcoreSpeed();
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

}
