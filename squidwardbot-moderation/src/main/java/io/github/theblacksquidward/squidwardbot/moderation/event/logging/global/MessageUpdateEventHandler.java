package io.github.theblacksquidward.squidwardbot.moderation.event.logging.global;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.models.MessageModel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Instant;

public class MessageUpdateEventHandler extends ListenerAdapter {

    private static final MongoCollection<MessageModel> MESSAGES_COLLECTION = SquidwardBot.getInstance().getMongoDatabase().getCollection("messages", MessageModel.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (event.getAuthor().isBot()) return;

        MESSAGES_COLLECTION.insertOne(
                new MessageModel(
                        new ObjectId(),
                        event.getGuild().getIdLong(),
                        event.getChannel().getIdLong(),
                        event.getAuthor().getIdLong(),
                        event.getMessageIdLong(),
                        event.getMessage().getContentDisplay(),
                        event.getMessage().getContentRaw()
                ));
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (event.getAuthor().isBot()) return;

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMessageUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(ColorConstants.GREEN_COLOR)
                .setTimestamp(Instant.now());

        MessageModel message = MESSAGES_COLLECTION.find(Filters.eq("message_id", event.getMessageIdLong()))
                .first();

        assert message != null;
        long messageId = message.message_id();
        long guildId = message.guild_id();
        long authorId = message.author_id();
        long channelId = message.channel_id();
        String rawContent = message.raw_content();

        Guild guild = event.getJDA().getGuildById(guildId);

        embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
        embedBuilder.setFooter("Message ID: " + messageId);
        embedBuilder.setDescription("**Message sent by " + guild.getMemberById(authorId).getAsMention() + " Edited in " + guild.getTextChannelById(channelId).getAsMention() + "**");
        embedBuilder.addField("Old Message", rawContent, false);
        embedBuilder.addField("New Message", event.getMessage().getContentRaw(), false);

        Bson updates = Updates.combine(Updates.set("content", event.getMessage().getContentDisplay()), Updates.set("raw_content", event.getMessage().getContentRaw()));

        MESSAGES_COLLECTION.updateOne(Filters.eq("message_id", messageId), updates);

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) return;

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMessageUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(ColorConstants.RED_COLOR)
                .setTimestamp(Instant.now());

        MessageModel message = MESSAGES_COLLECTION.find(Filters.eq("message_id", event.getMessageIdLong()))
                .first();

        assert message != null;
        long messageId = message.message_id();
        long guildId = message.guild_id();
        long authorId = message.author_id();
        long channelId = message.channel_id();
        String rawContent = message.raw_content();

        Guild guild = event.getJDA().getGuildById(guildId);

        embedBuilder.setAuthor(guild.getName(), null, guild.getIconUrl());
        embedBuilder.setFooter("Message ID: " + messageId);
        embedBuilder.setDescription("**Message sent by " + guild.getMemberById(authorId).getAsMention() + " Deleted in " + guild.getTextChannelById(channelId).getAsMention() + "**");
        embedBuilder.addField("Deleted Message", rawContent, false);

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
