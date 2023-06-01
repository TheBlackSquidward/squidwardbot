package io.github.theblacksquidward.squidwardbot.moderation.event.logging;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class MessageUpdateEventHandler extends ListenerAdapter {

    private static final String INSERT_USERS_SQL = "INSERT INTO messages (guild_id, channel_id, author_id, message_id, content, raw_content) VALUES (?, ?, ?, ?, ?, ?);";

    private static final String GET_MESSAGE_BY_MESSAGE_ID = "SELECT * FROM messages WHERE message_id=(?)";

    private static final String UPDATE_MESSAGE_BY_MESSAGE_ID = "UPDATE messages SET content=(?), raw_content=(?) WHERE message_id=(?)";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (event.getAuthor().isBot()) return;

        try {
            PreparedStatement preparedStatement = SquidwardBot.getInstance().getDatabaseConnection().prepareStatement(INSERT_USERS_SQL);
            preparedStatement.setLong(1, event.getGuild().getIdLong());
            preparedStatement.setLong(2, event.getChannel().getIdLong());
            preparedStatement.setLong(3, event.getAuthor().getIdLong());
            preparedStatement.setLong(4, event.getMessageIdLong());
            preparedStatement.setString(5, event.getMessage().getContentDisplay());
            preparedStatement.setString(6, event.getMessage().getContentRaw());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (event.getAuthor().isBot()) return;

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMessageUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(ColorConstants.GREEN_COLOR)
                .setTimestamp(Instant.now());

        try {
            PreparedStatement preparedStatement = SquidwardBot.getInstance().getDatabaseConnection().prepareStatement(GET_MESSAGE_BY_MESSAGE_ID);
            preparedStatement.setLong(1, event.getMessageIdLong());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) return;

            long guildId = resultSet.getLong("guild_id");
            long channelId = resultSet.getLong("channel_id");
            long authorId = resultSet.getLong("author_id");
            long messageId = resultSet.getLong("message_id");
            String rawContent = resultSet.getString("raw_content");

            Guild guild = event.getJDA().getGuildById(guildId);

            embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
            embedBuilder.setFooter("Message ID: " + messageId);
            embedBuilder.setDescription("**Message sent by " + guild.getMemberById(authorId).getAsMention() + " Edited in " + guild.getTextChannelById(channelId).getAsMention() + "**");
            embedBuilder.addField("Old Message", rawContent, false);
            embedBuilder.addField("New Message", event.getMessage().getContentRaw(), false);

            PreparedStatement preparedStatement1 = SquidwardBot.getInstance().getDatabaseConnection().prepareStatement(UPDATE_MESSAGE_BY_MESSAGE_ID);
            preparedStatement1.setString(1, event.getMessage().getContentDisplay());
            preparedStatement1.setString(2, event.getMessage().getContentRaw());
            preparedStatement1.setLong(3, event.getMessageIdLong());
            preparedStatement1.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) return;

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMessageUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(ColorConstants.RED_COLOR)
                .setTimestamp(Instant.now());

        try {
            PreparedStatement preparedStatement = SquidwardBot.getInstance().getDatabaseConnection().prepareStatement(GET_MESSAGE_BY_MESSAGE_ID);
            preparedStatement.setLong(1, event.getMessageIdLong());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) return;

            long guildId = resultSet.getLong("guild_id");
            long authorId = resultSet.getLong("author_id");
            long channelId = resultSet.getLong("channel_id");

            Guild guild = event.getJDA().getGuildById(guildId);

            embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
            embedBuilder.setFooter("Message ID: " + resultSet.getLong("message_id"));
            embedBuilder.setDescription("**Message sent by " + guild.getMemberById(authorId).getAsMention() + " Deleted in " + guild.getTextChannelById(channelId).getAsMention() + "**");
            embedBuilder.addField("Deleted Message", resultSet.getString("raw_content"), false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
