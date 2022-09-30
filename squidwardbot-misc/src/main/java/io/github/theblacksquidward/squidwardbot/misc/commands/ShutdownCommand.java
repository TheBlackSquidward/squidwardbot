package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ShutdownCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        if(event.getUser().getIdLong() == SquidwardBot.getInstance().getOwnerId()) {
            event.reply("Shutting down the bot...").queue();
            SquidwardBot.LOGGER.info("A shutdown has been scheduled via the execution of a discord command by the owner.");
            event.getJDA().shutdown();
            return;
        }
        event.reply("You do not have the required permissions to execute this command.").queue();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getDescription() {
        return "Shuts down the bot. Only the bot owner can execute this command.";
    }

}
