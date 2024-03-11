package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import java.time.Instant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class AbstractAudioCommand extends SquidwardBotCommand {

  protected static MessageEmbed createMusicReply(String text) {
    return new EmbedBuilder()
        .setTimestamp(Instant.now())
        .setColor(ColorConstants.PRIMARY_COLOR)
        .setDescription(text)
        .build();
  }
}
