package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;

public abstract class AbstractAudioCommand extends SquidwardBotCommand {

    protected static MessageEmbed createMusicReply(String text) {
        return new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setDescription(text)
                .build();
    }

}
