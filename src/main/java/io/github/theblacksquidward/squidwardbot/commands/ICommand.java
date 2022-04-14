package io.github.theblacksquidward.squidwardbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface ICommand {

    void onSlashCommand(SlashCommandInteractionEvent event);

    String getName();
    String getDescription();

    default CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription());
    }

}
