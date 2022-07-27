package io.github.theblacksquidward.squidwardbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.audio.DefaultAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.GuildAudioManager;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A useful helper class for all things audio.
 */
public class AudioUtils {

    //TODO log that

    //TODO document

    /**
     * A helper method that will load and play the given song into the TrackScheduler for that guild.
     * This does not check that an {@link TrackScheduler} exists for the given guild or if a player exists.
     * @param guild The guild to load the {@link AudioTrack}
     * @param identifier
     */
    public static void loadAndPlay(Guild guild, String identifier) {
        GuildAudioManager guildAudioManager = SquidwardBot.getGuildAudioManager();
        TrackScheduler trackScheduler = guildAudioManager.getTrackScheduler(guild);
        loadAndPlay(guild, identifier, new DefaultAudioLoadResultImpl(trackScheduler));
    }

    public static void loadAndPlay(Guild guild, String identifier, AudioLoadResultHandler audioLoadResult) {
        SquidwardBot.getGuildAudioManager().getPlayer(guild);
        GuildAudioManager guildAudioManager = SquidwardBot.getGuildAudioManager();
        guildAudioManager.getAudioPlayerManager().loadItemOrdered(guild, identifier, audioLoadResult);
    }

    /**
     * Gets the currently playing {@link AudioTrack} in the given guild.
     * If no {@link AudioTrack} is present, this WILL return null.
     * @param guild
     * @return The currently playing {@link AudioTrack}
     */
    @Nullable
    public static AudioTrack getCurrentAudioTrack(Guild guild) {
        return SquidwardBot.getGuildAudioManager().getPlayer(guild).getPlayingTrack();
    }

    /**
     * A helper method that allows for the currently playing AudioTrack to be skipped.
     * @param guild the guild where the track should be skipped
     */
    public static void skipTrack(Guild guild) {
        SquidwardBot.getGuildAudioManager().getPlayer(guild).stopTrack();
    }

    /**
     * Gets the player for a guild and pauses it.
     * NOTE: This method assumes that the guild is not null and that the guild currently has a player.
     * @param guild the guild where the track should be paused
     */
    public static void pauseTrack(Guild guild) {
        SquidwardBot.getGuildAudioManager().getPlayer(guild).setPaused(true);
    }

    /**
     * Gets the player for a guild and unpauses it.
     * NOTE: This method assumes that the guild is not null and that the guild currently has a player.
     * @param guild the guild where the track should be unpaused
     */
    public static void unpauseTrack(Guild guild) {
        SquidwardBot.getGuildAudioManager().getPlayer(guild).setPaused(false);
    }

    public static boolean isPlayerPaused(Guild guild) {
        return SquidwardBot.getGuildAudioManager().getPlayer(guild).isPaused();
    }

    public static boolean isQueueEmpty(Guild guild) {
        return SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).isQueueEmpty();
    }

    public static void shuffleQueue(Guild guild) {
        SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).shuffleQueue();
    }

    public static int getQueueSize(Guild guild) {
        return SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).getQueueSize();
    }

    public static List<AudioTrack> getQueuedTracks(Guild guild) {
        return SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).getQueuedTracks();
    }

    public static boolean hasPlayer(Guild guild) {
        return SquidwardBot.getGuildAudioManager().hasPlayer(guild);
    }

    public static String formatTrackTimeDuration(long timeInMilliseconds) {
        long hours = timeInMilliseconds / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMilliseconds / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMilliseconds % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
