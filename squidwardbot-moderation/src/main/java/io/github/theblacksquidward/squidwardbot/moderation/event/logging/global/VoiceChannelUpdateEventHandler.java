package io.github.theblacksquidward.squidwardbot.moderation.event.logging.global;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Instant;

public class VoiceChannelUpdateEventHandler extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        boolean hasJoined = event.getChannelJoined() != null;

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (hasJoined) {
            embedBuilder.setDescription("**" + member.getAsMention() + " joined voice channel " + event.getChannelJoined().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        } else {
            embedBuilder.setDescription("**" + member.getAsMention() + " left voice channel " + event.getChannelLeft().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildVoiceGuildMute(@Nonnull GuildVoiceGuildMuteEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean isGuildMuted = event.isGuildMuted();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (isGuildMuted) {
            embedBuilder.setDescription("**" + member.getAsMention() + " has been muted globally by a moderator.**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        } else {
            embedBuilder.setDescription("**" + member.getAsMention() + " has been unmuted globally by a moderator.**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildVoiceGuildDeafen(@Nonnull GuildVoiceGuildDeafenEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean isGuildDeafened = event.isGuildDeafened();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (isGuildDeafened) {
            embedBuilder.setDescription("**" + member.getAsMention() + " has been deafened globally by a moderator.**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        } else {
            embedBuilder.setDescription("**" + member.getAsMention() + " has been undeafened" +
                    " globally by a moderator.**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildVoiceSelfMute(@Nonnull GuildVoiceSelfMuteEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean isSelfMuted = event.isSelfMuted();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (isSelfMuted) {
            embedBuilder.setDescription("**" + member.getAsMention() + " has muted themselves in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        } else {
            embedBuilder.setDescription("**" + member.getAsMention() + " has unmuted themselves in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildVoiceSelfDeafen(@Nonnull GuildVoiceSelfDeafenEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean isSelfDeafened = event.isSelfDeafened();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (isSelfDeafened) {
            embedBuilder.setDescription("**" + member.getAsMention() + " has deafened themselves in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        } else {
            embedBuilder.setDescription("**" + member.getAsMention() + " has undeafened themselves in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildVoiceStream(@Nonnull GuildVoiceStreamEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean isStreaming = event.isStream();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (isStreaming) {
            embedBuilder.setDescription("**" + member.getAsMention() + " has started streaming in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        } else {
            if (event.getVoiceState().getChannel() == null) embedBuilder.setDescription("**" + member.getAsMention() + " has stopped streaming and left the voice channel they were in.**");
            else embedBuilder.setDescription("**" + member.getAsMention() + " has stopped streaming video in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildVoiceVideo(@Nonnull GuildVoiceVideoEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean isSendingVideo = event.isSendingVideo();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getGlobalLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getVoiceUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        if (isSendingVideo) {
            embedBuilder.setDescription("**" + member.getAsMention() + " has started sending video in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.GREEN_COLOR);
        } else {
            if (event.getVoiceState().getChannel() == null) embedBuilder.setDescription("**" + member.getAsMention() + " has stopped sending video and left the voice channel they were in.**");
            else embedBuilder.setDescription("**" + member.getAsMention() + " has stopped sending video in voice channel " + event.getVoiceState().getChannel().getAsMention() + "**");
            embedBuilder.setColor(ColorConstants.RED_COLOR);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
