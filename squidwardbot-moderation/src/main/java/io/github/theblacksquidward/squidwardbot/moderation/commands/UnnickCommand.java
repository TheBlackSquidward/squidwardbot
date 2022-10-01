package io.github.theblacksquidward.squidwardbot.moderation.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class UnnickCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {

    }

    @Override
    public String getName() {
        return "unnick";
    }

    @Override
    public String getDescription() {
        return "Removes the nickname of the user.";
    }

}
