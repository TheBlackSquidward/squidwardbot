package io.github.theblacksquidward.squidwardbot.audio.commands;

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

    protected static SongSearch.Hit getHit(Guild guild) {
        try {
            final SongSearch songSearch = AudioManager.getLyrics(guild);
            final LinkedList<SongSearch.Hit> hits = songSearch.getHits();
            return hits.isEmpty() ? null : hits.getFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
