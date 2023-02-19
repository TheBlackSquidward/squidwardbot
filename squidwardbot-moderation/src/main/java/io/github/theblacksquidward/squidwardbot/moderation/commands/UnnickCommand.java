package io.github.theblacksquidward.squidwardbot.moderation.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class UnnickCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        //TODO impl
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
