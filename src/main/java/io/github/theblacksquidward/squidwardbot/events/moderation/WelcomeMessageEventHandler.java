package io.github.theblacksquidward.squidwardbot.events.moderation;

import static java.time.temporal.ChronoField.*;

import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public class WelcomeMessageEventHandler extends SquidwardBotEventListener {

  // Tue, 3 Jun 2008 11:05:30 GMT
  public static final DateTimeFormatter CUSTOM_DATE_TIME_FORMATTER;

  static {
    Map<Long, String> days = new HashMap<>();
    days.put(1L, "Monday");
    days.put(2L, "Tuesday");
    days.put(3L, "Wednesday");
    days.put(4L, "Thursday");
    days.put(5L, "Friday");
    days.put(6L, "Saturday");
    days.put(7L, "Sunday");
    Map<Long, String> months = new HashMap<>();
    months.put(1L, "January");
    months.put(2L, "February");
    months.put(3L, "March");
    months.put(4L, "April");
    months.put(5L, "May");
    months.put(6L, "June");
    months.put(7L, "July");
    months.put(8L, "August");
    months.put(9L, "September");
    months.put(10L, "October");
    months.put(11L, "November");
    months.put(12L, "December");
    Map<Long, String> ampm = new HashMap<>();
    ampm.put(0L, "AM");
    ampm.put(1L, "PM");
    CUSTOM_DATE_TIME_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseLenient()
            .optionalStart()
            .appendText(DAY_OF_WEEK, days)
            .appendLiteral(", ")
            .optionalEnd()
            .appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral(' ')
            .appendText(MONTH_OF_YEAR, months)
            .appendLiteral(' ')
            .appendValue(YEAR, 4) // 2 digit year not handled
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(' ')
            .appendText(AMPM_OF_DAY, ampm)
            .toFormatter();
  }

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    Guild guild = event.getGuild();
    Member member = event.getMember();
    TextChannel channel = guild.getSystemChannel();
    EmbedBuilder embedBuilder =
        new EmbedBuilder()
            .setDescription("**" + member.getAsMention() + " has joined the server!**")
            .addField(
                "Account Created: ",
                member.getTimeCreated().format(CUSTOM_DATE_TIME_FORMATTER),
                false)
            .setImage(member.getEffectiveAvatarUrl())
            .setColor(ColorConstants.GREEN_COLOR)
            .setFooter("Member ID: " + member.getId())
            .setTimestamp(Instant.now());

    channel.sendMessageEmbeds(embedBuilder.build()).queue();
  }

  @Override
  public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
    Guild guild = event.getGuild();
    Member member = event.getMember();
    TextChannel channel = guild.getSystemChannel();
    EmbedBuilder embedBuilder =
        new EmbedBuilder()
            .setDescription("**" + member.getAsMention() + " has left the server!**")
            .setImage(member.getEffectiveAvatarUrl())
            .setColor(ColorConstants.RED_COLOR)
            .setFooter("Member ID: " + member.getId())
            .setTimestamp(Instant.now());

    channel.sendMessageEmbeds(embedBuilder.build()).queue();
  }
}
