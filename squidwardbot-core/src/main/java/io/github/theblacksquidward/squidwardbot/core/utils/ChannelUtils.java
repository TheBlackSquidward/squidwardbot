package io.github.theblacksquidward.squidwardbot.core.utils;

import net.dv8tion.jda.api.entities.channel.ChannelType;

public class ChannelUtils {

    public static  String formatChannelType(ChannelType channelType) {
        return switch (channelType) {
            case TEXT -> "Text Channel";
            case PRIVATE -> "Private Channel";
            case VOICE -> "Voice Channel";
            case CATEGORY -> "Category";
            case NEWS -> "News Channel";
            case STAGE -> "Stage Channel";
            case GUILD_NEWS_THREAD -> "News Thread";
            case GUILD_PUBLIC_THREAD -> "Public Thread";
            case GUILD_PRIVATE_THREAD -> "Private Thread";
            case FORUM -> "Forum Channel";
            default -> "Unknown Channel";
        };
    }

}
