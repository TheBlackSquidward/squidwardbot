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
import io.github.theblacksquidward.squidwardbot.audio.source.applemusic.AppleMusicSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.deezer.DeezerSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifySourceManager;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.Constants;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
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

    public static void init() {
        AUDIO_PLAYER_MANAGER.registerSourceManager(new SpotifySourceManager(AUDIO_PLAYER_MANAGER, SquidwardBot.getInstance().getSpotifyClientId(), SquidwardBot.getInstance().getSpotifyClientSecret()));
        AUDIO_PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        AUDIO_PLAYER_MANAGER.registerSourceManager(new DeezerSourceManager(SquidwardBot.getInstance().getDeezerMasterDecryptionKey()));
        AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        AUDIO_PLAYER_MANAGER.registerSourceManager(new AppleMusicSourceManager(AUDIO_PLAYER_MANAGER));
        AUDIO_PLAYER_MANAGER.registerSourceManager(new GeneralSearchSourceManager());
        AUDIO_PLAYER_MANAGER.getConfiguration().setFilterHotSwapEnabled(true);
    }

    public static GuildAudioManager getOrCreate(@NotNull Guild guild) {
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

    public static void requeueTrack(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().addTrackAtHead(getCurrentlyPlayingTrack(guild).makeClone());
    }

    public static boolean isRepeating(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isRepeating();
    }

    public static void toggleRepeating(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().toggleRepeating();
    }

    public static boolean isQueueEmpty(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isQueueEmpty();
    }

    public static int getQueueSize(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getQueueSize();
    }

    public static void shuffleQueue(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().shuffleQueue();
    }

    public static List<AudioTrack> getQueuedTracks(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getQueuedTracks();
    }

    public static void clearQueue(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().clearQueue();
    }

    public static int getPositionInQueue(@NotNull Guild guild, AudioTrack audioTrack) {
        return getOrCreate(guild).getTrackScheduler().getPositionInQueue(audioTrack);
    }

    public static AudioTrack getCurrentlyPlayingTrack(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getCurrentlyPlayingTrack();
    }

    public static AudioTrack skipTrack(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().skip();
    }

    public static boolean isPlayingTrack(@NotNull Guild guild) {
        return getOrCreate(guild).getAudioPlayer().getPlayingTrack() != null;
    }

    public static void pauseTrack(@NotNull Guild guild) {
        getOrCreate(guild).getAudioPlayer().setPaused(true);
    }

    public static void resumeTrack(@NotNull Guild guild) {
        getOrCreate(guild).getAudioPlayer().setPaused(false);
    }

    public static boolean isPaused(@NotNull Guild guild) {
        return getOrCreate(guild).getAudioPlayer().isPaused();
    }

    public static int getVolume(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getVolume();
    }

    public static void setVolume(@NotNull Guild guild, int volume) {
        getOrCreate(guild).getTrackScheduler().setVolume(volume);
    }

    public static boolean isBassBoosted(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isBassBoosted();
    }

    public static void enableBassBoost(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().enableBassBoost();
    }

    public static void disableBassBoost(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().disableBassBoost();
    }

    public static void setBassBoostMultiplier(@NotNull Guild guild, int percentage) {
        getOrCreate(guild).getTrackScheduler().setBassBoostMultiplier(percentage);
    }

    public static int getBassBoostPercentage(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getBassBoostPercentage();
    }

    public static boolean isNightcore(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isNightcore();
    }

    public static void enableNightcore(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().enableNightcore();
    }

    public static void disableNightcore(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().disableNightcore();
    }

    public static boolean isSlowed(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().isSlowed();
    }

    public static void enableSlowed(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().enableSlowed();
    }

    public static void disableSlowed(@NotNull Guild guild) {
        getOrCreate(guild).getTrackScheduler().disableSlowed();
    }

    public static void setTempo(@NotNull Guild guild, float tempo) {
        getOrCreate(guild).getTrackScheduler().setTempo(tempo);
    }

    public static double getTempo(@NotNull Guild guild) {
        return getOrCreate(guild).getTrackScheduler().getTempo();
    }

    public static SongSearch getLyrics(@NotNull Guild guild) throws IOException {
        return getLyrics(guild, getCurrentlyPlayingTrack(guild));
    }

    public static SongSearch getLyrics(@NotNull Guild guild, @NotNull AudioTrack audioTrack) throws IOException {
        return Constants.GENIUS_LYRICS_API.search(audioTrack.getInfo().title + "" + audioTrack.getInfo().author);
    }

    public static void loadAndPlay(@NotNull Guild guild, String identifier) {
        final GuildAudioManager guildAudioManager = getOrCreate(guild);
        loadAndPlay(guild, identifier, new BaseAudioLoadResultImpl(guildAudioManager.getTrackScheduler()));
    }

    public static void loadAndPlay(@NotNull Guild guild, String identifier, AudioLoadResultHandler audioLoadResult) {
        final GuildAudioManager guildAudioManager = getOrCreate(guild);
        AUDIO_PLAYER_MANAGER.loadItemOrdered(guildAudioManager, identifier, audioLoadResult);
    }

}
