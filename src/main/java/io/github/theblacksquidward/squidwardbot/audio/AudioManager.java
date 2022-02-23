package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifySourceManager;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioManager.class);

    private static final Map<String, Map.Entry<AudioPlayer, TrackScheduler>> players = new HashMap<>();
    private static AudioPlayerManager audioPlayerManager;

    public AudioManager() {
        LOGGER.info("Initializing Audio Manager...");
        audioPlayerManager = new DefaultAudioPlayerManager();
        registerRemoteSources(audioPlayerManager);
        LOGGER.info("Successfully initialized Audio Manger.");
    }

    private static void registerRemoteSources(AudioPlayerManager playerManager) {
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new SpotifySourceManager(SquidwardBot.SPOTIFY_API));
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new GetyarnAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));
    }

    public AudioPlayer getPlayer(Guild guild) {
        return hasPlayer(guild) ? players.get(guild.getId()).getKey() : createPlayer(guild);
    }

    public boolean hasPlayer(Guild guild) {
        return players.containsKey(guild.getId());
    }

    public TrackScheduler getTrackScheduler(Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    public void reset(Guild guild) {
        players.remove(guild.getId());
        getPlayer(guild).destroy();
        getTrackScheduler(guild).clearQueue();
        guild.getAudioManager().closeAudioConnection();
    }

    @Nullable
    public AudioTrack getCurrentAudioTrack(Guild guild) {
        return getPlayer(guild).getPlayingTrack();
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

    //TODO log that
    public static void loadAndPlay(Guild guild, String identifier) {
        SquidwardBot.AUDIO_MANAGER.getPlayer(guild);
        audioPlayerManager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                SquidwardBot.AUDIO_MANAGER.getTrackScheduler(guild).queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach((audioTrack) -> {
                    SquidwardBot.AUDIO_MANAGER.getTrackScheduler(guild).queue(audioTrack);
                });
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }

    public static void skipTrack(Guild guild) {
        SquidwardBot.AUDIO_MANAGER.getPlayer(guild).stopTrack();
    }

}
