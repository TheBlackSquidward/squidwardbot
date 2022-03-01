package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.audio.source.AudioSourceManagers;
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
    public AudioPlayer getPlayer(Guild guild) {
        return hasPlayer(guild) ? players.get(guild.getId()).getKey() : createPlayer(guild);
    }

    public boolean hasPlayer(Guild guild) {
        return players.containsKey(guild.getId());
    }

    public TrackScheduler getTrackScheduler(Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    public void removePlayer(Guild guild) {
        players.remove(guild.getId());
        getPlayer(guild).destroy();
        getTrackScheduler(guild).clearQueue();
        guild.getAudioManager().closeAudioConnection();
    }

    private AudioPlayer createPlayer(Guild guild) {
        AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(trackScheduler);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
        guild.getAudioManager().setSelfDeafened(true);
        players.put(guild.getId(), new AbstractMap.SimpleEntry<>(audioPlayer, trackScheduler));
        return audioPlayer;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

}
