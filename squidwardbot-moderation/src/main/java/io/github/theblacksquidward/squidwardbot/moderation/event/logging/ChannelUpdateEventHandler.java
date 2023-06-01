package io.github.theblacksquidward.squidwardbot.moderation.event.logging;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Instant;

public class ChannelUpdateEventHandler extends ListenerAdapter {

    @Override
    public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getChannelUpdateChannelId());

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**Channel Created: #" + event.getChannel().getName() + "**")
                .setColor(ColorConstants.GREEN_COLOR)
                .setFooter("Guild ID: " + guild.getId())
                .setTimestamp(Instant.now())
                .build();

        channel.sendMessageEmbeds(embed).queue();
    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getChannelUpdateChannelId());

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**Channel Deleted: #" + event.getChannel().getName() + "**")
                .setColor(ColorConstants.RED_COLOR)
                .setFooter("Guild ID: " + guild.getId())
                .setTimestamp(Instant.now())
                .build();

        channel.sendMessageEmbeds(embed).queue();
    }

}
