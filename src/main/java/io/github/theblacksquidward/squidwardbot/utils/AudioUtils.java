package io.github.theblacksquidward.squidwardbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.audio.AudioLoadResult;
import io.github.theblacksquidward.squidwardbot.audio.GuildAudioManager;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

/**
 * A useful helper class for all things audio.
 */
public class AudioUtils {

    //TODO log that

    //TODO document

    /**
     * A helper method that will load and play the given song into the TrackScheduler for that guild.
     * This does not check that an {@link TrackScheduler} exists for the given guild.
     * @param guild The guild to load the {@link AudioTrack}
     * @param identifier
     */
    public static void loadAndPlay(Guild guild, String identifier) {
        //TODO verify and move
        SquidwardBot.getGuildAudioManager().getPlayer(guild);

        GuildAudioManager guildAudioManager = SquidwardBot.getGuildAudioManager();
        TrackScheduler trackScheduler = guildAudioManager.getTrackScheduler(guild);
        guildAudioManager.getAudioPlayerManager().loadItemOrdered(guild, identifier, new AudioLoadResult(trackScheduler));
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

}
