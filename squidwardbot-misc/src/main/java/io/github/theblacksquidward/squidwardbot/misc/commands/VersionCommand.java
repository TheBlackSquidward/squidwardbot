package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.misc.utils.VersionUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class VersionCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(getVersionEmbed()).queue();
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Displays the current version of the bot.";
    }

    private MessageEmbed getVersionEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
        embedBuilder.setDescription("Current Version: `" + SquidwardBot.getInstance().getVersion() + "`\n" +
                "Latest Version: `" + VersionUtils.getLatestVersion() + "`\n");
        return embedBuilder.build();
    }

}
