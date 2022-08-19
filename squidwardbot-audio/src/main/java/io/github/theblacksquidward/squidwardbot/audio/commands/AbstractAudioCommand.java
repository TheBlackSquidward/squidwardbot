package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class AbstractAudioCommand extends SquidwardBotCommand {

    protected static MessageEmbed createMusicReply(String text) {
        return new EmbedBuilder()
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setAuthor("|  " + text, null, "https://avatars.githubusercontent.com/u/65785034?v=4")
                .build();
    }

}
