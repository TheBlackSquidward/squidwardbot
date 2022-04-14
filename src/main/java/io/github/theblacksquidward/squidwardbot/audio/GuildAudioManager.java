package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import io.github.theblacksquidward.squidwardbot.audio.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for
 */
public class GuildAudioManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildAudioManager.class);

    private static final Map<String, Map.Entry<AudioPlayer, TrackScheduler>> players = new HashMap<>();
    private static AudioPlayerManager audioPlayerManager;

    public GuildAudioManager() {
        LOGGER.info("Initializing Guild Audio Manager...");
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        LOGGER.info("Successfully initialized Audio Manger.");
    }

    @NotNull
    public AudioPlayer getOrCreatePlayer(Guild guild) {
        return hasPlayer(guild) ? players.get(guild.getId()).getKey() : createPlayer(guild);
    }

    @Nullable
    public AudioPlayer getPlayer(Guild guild) {
        return hasPlayer(guild) ? players.get(guild.getId()).getKey() : null;
    }

    public boolean hasPlayer(@NotNull Guild guild) {
        return players.containsKey(guild.getId());
    }

    public TrackScheduler getTrackScheduler(@NotNull Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    public void removePlayer(@NotNull Guild guild) {
        getPlayer(guild).destroy();
        getTrackScheduler(guild).clearQueue();
        players.remove(guild.getId());
        guild.getAudioManager().closeAudioConnection();
        LOGGER.info("Closed an audio connection and removed audio player for " + guild.getName());
    }

    public void openAudioConnection(Guild guild, AudioChannel audioChannel) {
        getOrCreatePlayer(guild);
        guild.getAudioManager().setSelfDeafened(true);
        guild.getAudioManager().openAudioConnection(audioChannel);
        LOGGER.info("Opened an audio connection for " + guild.getName());
    }

    @NotNull
    private  AudioPlayer createPlayer(@NotNull Guild guild) {
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(trackScheduler);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
        guild.getAudioManager().setSelfDeafened(true);
        players.put(guild.getId(), new AbstractMap.SimpleEntry<>(audioPlayer, trackScheduler));
        LOGGER.info("Created an audio player for " + guild.getName());
        return audioPlayer;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

}
