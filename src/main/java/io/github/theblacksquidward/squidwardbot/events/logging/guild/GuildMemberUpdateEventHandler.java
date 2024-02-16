package io.github.theblacksquidward.squidwardbot.events.logging.guild;

import com.mongodb.client.model.Filters;
import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import io.github.theblacksquidward.squidwardbot.database.SquidwardBotDatabase;
import io.github.theblacksquidward.squidwardbot.database.models.GuildModel;
import java.time.Instant;
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
import org.jetbrains.annotations.NotNull;

@EventListener
public class GuildMemberUpdateEventHandler extends SquidwardBotEventListener {

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    Member member = event.getMember();
    User user = event.getUser();
    Guild guild = event.getGuild();
    GuildModel guildModel =
        SquidwardBotDatabase.getInstance()
            .getGuildCollection()
            .find(Filters.eq("guild_id", guild.getIdLong()))
            .first();

    assert guildModel != null;
    if (guildModel.member_update_logging_channel_id() == null) return;

    TextChannel channel = guild.getTextChannelById(guildModel.member_update_logging_channel_id());

    MessageEmbed embed =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setTitle("**A New Guild Member has Joined the Server: " + guild.getName() + "!**")
            .setDescription("**Guild Member Joined: " + member.getAsMention() + "**")
            .addField("Member Username", user.getName(), true)
            .addField("Member ID", member.getId(), true)
            .addField("Account Created", user.getTimeCreated().toString(), false)
            .setThumbnail(member.getEffectiveAvatarUrl())
            .setColor(ColorConstants.GREEN_COLOR)
            .setFooter("Guild ID: " + guild.getId())
            .setTimestamp(Instant.now())
            .build();

    channel.sendMessageEmbeds(embed).queue();
  }

  @Override
  public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
    Member member = event.getMember();
    User user = event.getUser();
    Guild guild = event.getGuild();
    GuildModel guildModel =
        SquidwardBotDatabase.getInstance()
            .getGuildCollection()
            .find(Filters.eq("guild_id", guild.getIdLong()))
            .first();

    assert guildModel != null;
    if (guildModel.member_update_logging_channel_id() == null) return;

    TextChannel channel = guild.getTextChannelById(guildModel.member_update_logging_channel_id());

    EmbedBuilder embedBuilder =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setTitle("**A Guild Member has Left the Server: " + guild.getName() + "!**")
            .setColor(ColorConstants.RED_COLOR)
            .setThumbnail(member.getEffectiveAvatarUrl())
            .setFooter("Guild ID: " + guild.getId())
            .setTimestamp(Instant.now());

    guild
        .retrieveAuditLogs()
        .limit(1)
        .queue(
            logs -> {
              if (logs.isEmpty()) return;
              AuditLogEntry log = logs.get(0);
              switch (log.getType()) {
                case BAN -> {
                  embedBuilder.addField("Member Banned", user.getAsMention(), false);
                  embedBuilder.addField("Banned By", log.getUser().getAsMention(), false);
                  if (log.getReason() != null)
                    embedBuilder.addField("Reason", log.getReason(), false);
                }
                case KICK -> {
                  embedBuilder.addField("Member Kicked", user.getAsMention(), false);
                  embedBuilder.addField("Kicked By", log.getUser().getAsMention(), false);
                  if (log.getReason() != null)
                    embedBuilder.addField("Reason", log.getReason(), false);
                }
                default -> embedBuilder.addField("Member Left", user.getAsMention(), false);
              }
              embedBuilder
                  .addField("Member Username", user.getName(), true)
                  .addField("Member ID", member.getId(), true);
              channel.sendMessageEmbeds(embedBuilder.build()).queue();
            });
  }

  @Override
  public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
    Member member = event.getMember();
    Guild guild = event.getGuild();

    int count = event.getRoles().size();
    GuildModel guildModel =
        SquidwardBotDatabase.getInstance()
            .getGuildCollection()
            .find(Filters.eq("guild_id", guild.getIdLong()))
            .first();

    assert guildModel != null;
    if (guildModel.member_update_logging_channel_id() == null) return;

    TextChannel channel = guild.getTextChannelById(guildModel.member_update_logging_channel_id());

    EmbedBuilder embedBuilder =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setTitle(
                "**A Guild Member Has Been Given Some New Roles in the Server: "
                    + guild.getName()
                    + "**")
            .setDescription(
                "**"
                    + member.getAsMention()
                    + " has been given "
                    + count
                    + " new role"
                    + (count == 1 ? "" : "s")
                    + "!**")
            .addField("Member Username", member.getUser().getName(), true)
            .addField("Member ID", member.getId(), true)
            .setColor(member.getColor())
            .setFooter("Guild ID: " + guild.getId())
            .setTimestamp(Instant.now());

    StringBuilder stringBuilder = new StringBuilder();
    event.getRoles().forEach(role -> stringBuilder.append(role.getAsMention()).append("\n"));
    event
        .getRoles()
        .forEach(role -> embedBuilder.addField("Roles Given", stringBuilder.toString(), false));

    guild
        .retrieveAuditLogs()
        .limit(1)
        .queue(
            logs -> {
              if (logs.isEmpty() || logs.get(0).getType() != ActionType.MEMBER_ROLE_UPDATE) {
                channel.sendMessageEmbeds(embedBuilder.build()).queue();
                return;
              }
              AuditLogEntry log = logs.get(0);
              embedBuilder.addField("Given By", log.getUser().getAsMention(), false);
              channel.sendMessageEmbeds(embedBuilder.build()).queue();
            });
  }

  @Override
  public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
    Member member = event.getMember();
    Guild guild = event.getGuild();

    int count = event.getRoles().size();
    GuildModel guildModel =
        SquidwardBotDatabase.getInstance()
            .getGuildCollection()
            .find(Filters.eq("guild_id", guild.getIdLong()))
            .first();

    assert guildModel != null;
    if (guildModel.member_update_logging_channel_id() == null) return;

    TextChannel channel = guild.getTextChannelById(guildModel.member_update_logging_channel_id());

    EmbedBuilder embedBuilder =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setTitle(
                "**A Guild Member Has Had Roles Removed in the Server: " + guild.getName() + "**")
            .setDescription(
                "**"
                    + member.getAsMention()
                    + " has had "
                    + count
                    + " role"
                    + (count == 1 ? "" : "s")
                    + " removed!**")
            .addField("Member Username", member.getUser().getName(), true)
            .addField("Member ID", member.getId(), true)
            .setColor(member.getColor())
            .setFooter("Guild ID: " + guild.getId())
            .setTimestamp(Instant.now());

    StringBuilder stringBuilder = new StringBuilder();
    event.getRoles().forEach(role -> stringBuilder.append(role.getAsMention()).append("\n"));
    event
        .getRoles()
        .forEach(role -> embedBuilder.addField("Roles Removed", stringBuilder.toString(), false));

    guild
        .retrieveAuditLogs()
        .limit(1)
        .queue(
            logs -> {
              if (logs.isEmpty() || logs.get(0).getType() != ActionType.MEMBER_ROLE_UPDATE) {
                channel.sendMessageEmbeds(embedBuilder.build()).queue();
                return;
              }
              AuditLogEntry log = logs.get(0);
              embedBuilder.addField("Removed By", log.getUser().getAsMention(), false);
              channel.sendMessageEmbeds(embedBuilder.build()).queue();
            });
  }
}
