package io.github.theblacksquidward.squidwardbot.events.logging.global;

import io.github.theblacksquidward.squidwardbot.Environment;
import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import io.github.theblacksquidward.squidwardbot.utils.ChannelUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;

import javax.annotation.Nonnull;
import java.time.Instant;

@EventListener
public class GlobalChannelUpdateEventHandler extends SquidwardBotEventListener {

    @Override
    public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(Environment.getInstance().getHarryServerId())
                .getTextChannelById(Environment.getInstance().getGlobalChannelUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
        embedBuilder.setDescription("**Channel Created: #" + event.getChannel().getAsMention() + "**");
        embedBuilder.addField("Channel Type", ChannelUtils.formatChannelType(event.getChannelType()), true);
        embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        embedBuilder.setFooter("Guild ID: " + guild.getId());
        embedBuilder.setTimestamp(Instant.now());

        guild.retrieveAuditLogs()
                .limit(1)
                .queue(logs -> {
                    if (logs.isEmpty()) return;
                    embedBuilder.addField("Created By", logs.get(0).getUser().getAsMention(), true);
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                });
    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(Environment.getInstance().getHarryServerId())
                .getTextChannelById(Environment.getInstance().getGlobalChannelUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
        embedBuilder.setDescription("**Channel Deleted: #" + event.getChannel().getName() + "**");
        embedBuilder.addField("Channel Type", ChannelUtils.formatChannelType(event.getChannelType()), true);
        embedBuilder.setColor(ColorConstants.RED_COLOR);
        embedBuilder.setFooter("Guild ID: " + guild.getId());
        embedBuilder.setTimestamp(Instant.now());

        guild.retrieveAuditLogs()
                .limit(1)
                .queue(logs -> {
                    if (logs.isEmpty()) return;
                    embedBuilder.addField("Deleted By", logs.get(0).getUser().getAsMention(), true);
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                });
    }

}
