package io.github.theblacksquidward.squidwardbot.moderation.event.logging.guild;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.models.GuildModel;
import io.github.theblacksquidward.squidwardbot.core.utils.ChannelUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.Instant;

public class GuildChannelUpdateEventHandler extends ListenerAdapter {

    private static final MongoCollection<GuildModel> guildCollection = SquidwardBot.getInstance().getMongoDatabase().getCollection("guilds", GuildModel.class);

    @Override
    public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
        Guild guild = event.getGuild();
        GuildModel guildModel = guildCollection.find(Filters.eq("guild_id", guild.getIdLong())).first();

        assert guildModel != null;
        if (guildModel.channel_update_logging_channel_id() == null) return;

        TextChannel channel = guild
                .getTextChannelById(guildModel.channel_update_logging_channel_id());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
        embedBuilder.setDescription("**Channel Created: #" + event.getChannel().getAsMention() + "**");
        embedBuilder.addField("Channel Type", ChannelUtils.formatChannelType(event.getChannelType()), true);
        embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        embedBuilder.setFooter("Guild ID: " + guild.getId());
        embedBuilder.setTimestamp(Instant.now());

        guild.retrieveAuditLogs()
                .type(ActionType.CHANNEL_CREATE)
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
        GuildModel guildModel = guildCollection.find(Filters.eq("guild_id", guild.getIdLong())).first();

        assert guildModel != null;
        if (guildModel.channel_update_logging_channel_id() == null) return;

        TextChannel channel = guild
                .getTextChannelById(guildModel.channel_update_logging_channel_id());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
        embedBuilder.setDescription("**Channel Deleted: #" + event.getChannel().getName() + "**");
        embedBuilder.addField("Channel Type", ChannelUtils.formatChannelType(event.getChannelType()), true);
        embedBuilder.setColor(ColorConstants.RED_COLOR);
        embedBuilder.setFooter("Guild ID: " + guild.getId());
        embedBuilder.setTimestamp(Instant.now());

        guild.retrieveAuditLogs()
                .type(ActionType.CHANNEL_DELETE)
                .limit(1)
                .queue(logs -> {
                    if (logs.isEmpty()) return;
                    embedBuilder.addField("Deleted By", logs.get(0).getUser().getAsMention(), true);
                    channel.sendMessageEmbeds(embedBuilder.build()).queue();
                });
    }

}
