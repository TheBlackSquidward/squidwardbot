package io.github.theblacksquidward.squidwardbot.events.logging.global;

import io.github.theblacksquidward.squidwardbot.Environment;
import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import io.github.theblacksquidward.squidwardbot.utils.ColorUtils;
import java.time.Instant;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public class RoleUpdateEventHandler extends SquidwardBotEventListener {

  @Override
  public void onRoleCreate(@Nonnull RoleCreateEvent event) {
    Role role = event.getRole();
    Guild guild = event.getGuild();

    TextChannel channel =
        event
            .getJDA()
            .getGuildById(Environment.getInstance().getHarryServerId())
            .getTextChannelById(Environment.getInstance().getGlobalRoleUpdateChannelId());

    MessageEmbed embed =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setDescription("**" + "Role Created: " + role.getName() + "**")
            .setColor(ColorConstants.GREEN_COLOR)
            .setFooter("Role ID: " + role.getId())
            .setTimestamp(Instant.now())
            .build();

    channel.sendMessageEmbeds(embed).queue();
  }

  @Override
  public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
    Role role = event.getRole();
    Guild guild = event.getGuild();

    TextChannel channel =
        event
            .getJDA()
            .getGuildById(Environment.getInstance().getHarryServerId())
            .getTextChannelById(Environment.getInstance().getGlobalRoleUpdateChannelId());

    MessageEmbed embed =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setDescription("**" + "Role Deleted: " + role.getName() + "**")
            .setColor(ColorConstants.RED_COLOR)
            .setFooter("Role ID: " + role.getId())
            .setTimestamp(Instant.now())
            .build();

    channel.sendMessageEmbeds(embed).queue();
  }

  @Override
  public void onRoleUpdateColor(@NotNull RoleUpdateColorEvent event) {
    Role role = event.getRole();
    Guild guild = event.getGuild();

    TextChannel channel =
        event
            .getJDA()
            .getGuildById(Environment.getInstance().getHarryServerId())
            .getTextChannelById(Environment.getInstance().getGlobalRoleUpdateChannelId());

    MessageEmbed embed =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setDescription(
                "**"
                    + "Role Color Changed for "
                    + role.getName()
                    + ": "
                    + ColorUtils.toHexString(event.getOldColor())
                    + " -> "
                    + ColorUtils.toHexString(event.getNewColor())
                    + "**")
            .setColor(event.getNewColor())
            .setFooter("Role ID: " + role.getId())
            .setTimestamp(Instant.now())
            .build();

    channel.sendMessageEmbeds(embed).queue();
  }

  @Override
  public void onRoleUpdateName(@NotNull RoleUpdateNameEvent event) {
    Role role = event.getRole();
    Guild guild = event.getGuild();

    TextChannel channel =
        event
            .getJDA()
            .getGuildById(Environment.getInstance().getHarryServerId())
            .getTextChannelById(Environment.getInstance().getGlobalRoleUpdateChannelId());

    MessageEmbed embed =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setDescription(
                "**"
                    + "Role Name Changed From: "
                    + event.getOldName()
                    + " -> "
                    + event.getNewName()
                    + "**")
            .setColor(role.getColor())
            .setFooter("Role ID: " + role.getId())
            .setTimestamp(Instant.now())
            .build();

    channel.sendMessageEmbeds(embed).queue();
  }

  @Override
  public void onRoleUpdatePermissions(@NotNull RoleUpdatePermissionsEvent event) {
    Role role = event.getRole();
    Guild guild = event.getGuild();

    TextChannel channel =
        event
            .getJDA()
            .getGuildById(Environment.getInstance().getHarryServerId())
            .getTextChannelById(Environment.getInstance().getGlobalRoleUpdateChannelId());

    EmbedBuilder embedBuilder =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setDescription("**" + "Role Permissions Changed For: " + role.getName() + "**")
            .setColor(role.getColor())
            .setFooter("Role ID: " + role.getId())
            .setTimestamp(Instant.now());

    event
        .getOldPermissions()
        .forEach(permission -> embedBuilder.addField(permission.getName(), "Removed", false));
    event
        .getNewPermissions()
        .forEach(permission -> embedBuilder.addField(permission.getName(), "Added", false));

    channel.sendMessageEmbeds(embedBuilder.build()).queue();
  }

  @Override
  public void onRoleUpdateIcon(@NotNull RoleUpdateIconEvent event) {
    Role role = event.getRole();
    Guild guild = event.getGuild();

    TextChannel channel =
        event
            .getJDA()
            .getGuildById(Environment.getInstance().getHarryServerId())
            .getTextChannelById(Environment.getInstance().getGlobalRoleUpdateChannelId());

    MessageEmbed embed =
        new EmbedBuilder()
            .setAuthor(guild.getName(), null, guild.getIconUrl())
            .setDescription("**" + "Role Icon Changed For : " + role.getName() + "**")
            .setImage(event.getNewIcon().getIconUrl())
            .setColor(role.getColor())
            .setFooter("Role ID: " + role.getId())
            .setTimestamp(Instant.now())
            .build();

    channel.sendMessageEmbeds(embed).queue();
  }
}
