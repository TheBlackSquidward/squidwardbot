package io.github.theblacksquidward.squidwardbot.moderation.event.logging;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MemberUpdateEventHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        User user = event.getUser();
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMemberUpdateChannelId());

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**Member Joined: " + member.getAsMention() + "**")
                .addField("Account Age", user.getTimeCreated().toString(), false)
                .setImage(member.getEffectiveAvatarUrl())
                .setColor(ColorConstants.GREEN_COLOR)
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now())
                .build();

        channel.sendMessageEmbeds(embed).queue();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMemberUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setColor(ColorConstants.RED_COLOR)
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        guild.retrieveAuditLogs()
                .queueAfter(1, TimeUnit.SECONDS, (logs) -> {
                            for (AuditLogEntry log : logs) {
                                if (log.getTargetIdLong() == event.getUser().getIdLong()) {
                                    if (log.getType() == ActionType.BAN) {
                                        embedBuilder.setDescription("**" + member.getAsMention() + " was banned by " + log.getUser().getAsMention() + "**");
                                        if (log.getReason() != null) embedBuilder.addField("Reason", log.getReason(), false);
                                    } else if (log.getType() == ActionType.KICK) {
                                        embedBuilder.setDescription("**" + member.getAsMention() + " was kicked by " + log.getUser().getAsMention() + "**");
                                        if (log.getReason() != null) embedBuilder.addField("Reason", log.getReason(), false);
                                    } else {
                                        embedBuilder.setDescription("**Member Left " + member.getAsMention() + "**");
                                    }
                                    break;
                                }
                            }
                            channel.sendMessageEmbeds(embedBuilder.build()).queue();
                        }
                );
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMemberUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**" + member.getAsMention() + " was given the following roles:**")
                .setColor(member.getColor())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());
        event.getRoles().forEach(role -> embedBuilder.addField(role.getName(), "", true));

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMemberUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**" + member.getAsMention() + " was removed the following roles:**")
                .setColor(member.getColor())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());
        event.getRoles().forEach(role -> embedBuilder.addField("", role.getName(), true));

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildMemberUpdateAvatar(@NotNull GuildMemberUpdateAvatarEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMemberUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**" + member.getAsMention() + " changed their avatar:**")
                .addField("Old Avatar", event.getOldAvatarUrl(), false)
                .addField("New Avatar", event.getNewAvatarUrl(), false)
                .setColor(member.getColor())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        TextChannel channel = event.getJDA()
                .getGuildById(SquidwardBot.getInstance().getLoggingServerId())
                .getTextChannelById(SquidwardBot.getInstance().getMemberUpdateChannelId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setDescription("**" + member.getAsMention() + " changed their nickname:**")
                .addField("Old Nickname", event.getOldNickname(), false)
                .addField("New Nickname", event.getNewNickname(), false)
                .setColor(member.getColor())
                .setFooter("Member ID: " + member.getId())
                .setTimestamp(Instant.now());

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
