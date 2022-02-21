package io.github.theblacksquidward.squidwardbot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildUtils {


    @Nullable
    public static VoiceChannel matchVoiceChannel(@NotNull Guild guild, String voiceChannelName) {
        VoiceChannel result = null;
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            String formattedString = voiceChannel.getName().replaceAll("\\p{P}", "");
            if (formattedString.equalsIgnoreCase(voiceChannelName)) {
                result = voiceChannel;
            }
        }
        return result;
    }

}
