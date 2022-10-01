package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import genius.SongSearch;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;

public abstract class AbstractAudioCommand extends SquidwardBotCommand {

    protected static MessageEmbed createMusicReply(String text) {
        return new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setDescription(text)
                .build();
    }

    protected static SongSearch.Hit getCurrentlyPlayingHit(Guild guild) {
        try {
            final SongSearch songSearch = AudioManager.getLyrics(guild);
            final LinkedList<SongSearch.Hit> hits = songSearch.getHits();
            return hits.isEmpty() ? null : hits.getFirst();
        } catch (IOException e) {
            //TODO this needs to be logged
            throw new RuntimeException(e);
        }
    }


    protected static LinkedList<SongSearch.Hit> getHits(Guild guild, AudioTrack audioTrack) {
        try {
            final SongSearch songSearch = AudioManager.getLyrics(guild, audioTrack);
            return songSearch.getHits();
        } catch (IOException e) {
            //TODO change this to be logged
            throw new RuntimeException(e);
        }
    }

    protected static SongSearch.Hit getHit(Guild guild, AudioTrack audioTrack) {
        try {
            final SongSearch songSearch = AudioManager.getLyrics(guild, audioTrack);
            final LinkedList<SongSearch.Hit> hits = songSearch.getHits();
            return hits.isEmpty() ? null : hits.getFirst();
        } catch (IOException e) {
            //TODO change this to be logged
            throw new RuntimeException(e);
        }
    }

}
