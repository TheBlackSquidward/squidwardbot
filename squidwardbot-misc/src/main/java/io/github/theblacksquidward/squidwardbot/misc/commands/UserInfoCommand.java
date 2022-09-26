package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Command
public class UserInfoCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        OptionMapping option = event.getOption("user");
        event.deferReply().queue();
        if(!event.isFromGuild()) {
            event.getHook().sendMessage("You must be in a server to execute this command.").queue();
        }
        if(option == null) {
            event.getHook().sendMessageEmbeds(createUserEmbed(event.getMember())).queue();
            return;
        }
        Member member = option.getAsMember();
        event.getHook().sendMessageEmbeds(createUserEmbed(member)).queue();
    }

    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return "Returns information about the user who executed the command or the specified user.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(new OptionData(OptionType.USER, "user", "The specified user.", false));
    }

    private MessageEmbed createUserEmbed(Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(member.getUser().getName() + "#" + member.getUser().getDiscriminator());
        embedBuilder.setColor(member.getColor());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setThumbnail(member.getEffectiveAvatarUrl());

        embedBuilder.addField("Nickname", member.getNickname() == null ? "N/A" : member.getNickname(), false);
        embedBuilder.addField("Mention", member.getAsMention(), false);
        embedBuilder.addField("Online Status", convertOnlineStatus(member.getOnlineStatus()), false);

        if(!member.getRoles().isEmpty()) {
            final StringBuilder roles = new StringBuilder();
            member.getRoles().forEach(role -> roles.append(role.getAsMention()).append(", "));
            roles.delete(roles.length() - 2, roles.length());
            embedBuilder.addField("Roles", roles.toString(), false);
        }

        if(!member.getPermissions().isEmpty()) {
            final StringBuilder permissions = new StringBuilder();
            member.getPermissions().forEach(perm -> permissions.append("`").append(perm.getName()).append("`, "));
            permissions.delete(permissions.length() - 2, permissions.length());
            embedBuilder.addField("Permissions", permissions.toString(), false);
        }

        if (member.isBoosting()) {
            embedBuilder.addField("Time Boosted", StringUtils.formatTime(member.getTimeBoosted()), false);
        }

        if (member.isTimedOut()) {
            embedBuilder.addField("Timeout End", StringUtils.formatTime(member.getTimeOutEnd()), false);
        }

        embedBuilder.addField("Created At", StringUtils.formatTime(member.getTimeCreated()), false );
        embedBuilder.addField("Joined At", StringUtils.formatTime(member.getTimeJoined()), false);

        embedBuilder.addField("Is Owner?", getBooleanAsYesNo(member.isOwner()), true);
        embedBuilder.addField("Is Bot?", getBooleanAsYesNo(member.getUser().isBot()), true);
        embedBuilder.addField("Is System?", getBooleanAsYesNo(member.getUser().isSystem()), true);

        embedBuilder.setFooter("User ID: " + member.getIdLong());

        return embedBuilder.build();
    }

    private String getBooleanAsYesNo(boolean bool) {
        return bool ? "Yes" : "No";
    }

    private String convertOnlineStatus(@NotNull OnlineStatus onlineStatus) {
        return switch (onlineStatus) {
            case DO_NOT_DISTURB -> "Do Not Disturb";
            case IDLE -> "Idle";
            case INVISIBLE -> "Invisible";
            case OFFLINE -> "Offline";
            case ONLINE -> "Online";
            case UNKNOWN -> "Unknown";
        };
    }

}
