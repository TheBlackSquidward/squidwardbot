package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nullable;
import java.time.Instant;

public class BotInfoCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(createBotInfoEmbed(event.getJDA(), event.isFromGuild() ? event.getGuild() : null)).queue();
    }

    @Override
    public String getName() {
        return "botinfo";
    }

    @Override
    public String getDescription() {
        return "Returns information about the bot.";
    }

    private MessageEmbed createBotInfoEmbed(JDA jda, @Nullable Guild guild) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final boolean isFromGuild = guild != null;
        embedBuilder.setTitle("Bot Information");
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setColor(isFromGuild ? guild.getSelfMember().getColor() : ColorConstants.PRIMARY_COLOR);
        embedBuilder.addField("Name", isFromGuild ? guild.getSelfMember().getEffectiveName() : jda.getSelfUser().getName(), false);
        embedBuilder.addField("Created On", StringUtils.formatTime(jda.getSelfUser().getTimeCreated()), false);
        if(isFromGuild) {
            embedBuilder.addField("Joined At", StringUtils.formatTime(guild.getSelfMember().getTimeJoined()), false);
        }

        embedBuilder.addField("", "**__Counts__**:", false);
        embedBuilder.addField("Members", String.valueOf(jda.getUserCache().size()), true);
        embedBuilder.addField("Roles", String.valueOf(jda.getRoleCache().size()), true);
        embedBuilder.addField("Emojis", String.valueOf(jda.getEmojiCache().size()), true);
        embedBuilder.addField("Servers", String.valueOf(jda.getGuildCache().size()), true);
        embedBuilder.addField("Categories", String.valueOf(jda.getCategoryCache().size()), true);
        embedBuilder.addField("Channels", String.valueOf(jda.getVoiceChannelCache().size() + jda.getTextChannelCache().size()
                + jda.getThreadChannelCache().size()), true);
        embedBuilder.addField("Slash Commands", String.valueOf(CommandRegistry.getCommandsSize()), true);
        embedBuilder.addField("Bot Owner", jda.getUserById(SquidwardBot.getInstance().getOwnerId()).getAsMention(), false);

        embedBuilder.setThumbnail(jda.getSelfUser().getEffectiveAvatarUrl());
        return embedBuilder.build();
    }

}
