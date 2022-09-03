package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@Command
public class UptimeCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        final long uptimeInMilliseconds = runtimeMXBean.getUptime();
        final String uptimeFormatted = StringUtils.millisecondsFormatted(uptimeInMilliseconds);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
        embedBuilder.setDescription("The current uptime of the bot is: **" + uptimeFormatted + "**.");
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getName() {
        return "uptime";
    }

    @Override
    public String getDescription() {
        return "Displays the current uptime of the bot.";
    }

}
