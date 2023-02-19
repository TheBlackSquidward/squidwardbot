package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Instant;

public class ServerInfoCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        event.deferReply().queue();
        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can only be executed in a server.").mentionRepliedUser(false).setEphemeral(true).queue();
            return;
        }
        event.getHook().sendMessageEmbeds(getServerEmbed(guild)).mentionRepliedUser(false).queue();
    }

    @Override
    public String getName() {
        return "serverinfo";
    }

    @Override
    public String getDescription() {
        return "Returns information about the server in which the command is executed.";
    }

    private MessageEmbed getServerEmbed(Guild guild) {
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(guild.getName());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setThumbnail(guild.getIconUrl());
        embedBuilder.setDescription(guild.getDescription());
        embedBuilder.setColor(ColorConstants.SECONDARY_COLOR);

        embedBuilder.addField("Members", String.valueOf(guild.getMemberCount()), true);
        embedBuilder.addField("Boosts", String.valueOf(guild.getBoostCount()), true);
        embedBuilder.addField("Emotes", String.valueOf(guild.getEmojis().size()), true);
        embedBuilder.addField("Categories", String.valueOf(guild.getCategories().size()), true);
        embedBuilder.addField("Text Channels", String.valueOf(guild.getTextChannels().size()), true);
        embedBuilder.addField("Voice Channels", String.valueOf(guild.getVoiceChannels().size()), true);

        embedBuilder.addField("Created By", guild.getOwner().getAsMention(), false);
        embedBuilder.addField("Created At", StringUtils.formatTime(guild.getTimeCreated()), false);

        embedBuilder.setFooter("Server ID: " + guild.getIdLong());

        return embedBuilder.build();
    }

}
