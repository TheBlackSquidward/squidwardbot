package io.github.theblacksquidward.squidwardbot.utils;

import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {

    //TODO WIP image
    public static MessageEmbed createMusicReply(String text) {
        return new EmbedBuilder()
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setAuthor("|  " + text, null, "https://avatars.githubusercontent.com/u/65785034?v=4")
                .build();
    }

}
