package io.github.theblacksquidward.squidwardbot.commands.misc;

import io.github.theblacksquidward.squidwardbot.Environment;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.constants.Constants;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ShutdownCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        if(event.getUser().getIdLong() == Environment.getInstance().getOwnerId()) {
            event.reply("Shutting down the bot...").queue();
            Constants.LOGGER.info("A shutdown has been scheduled via the execution of a discord command by the owner.");
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
