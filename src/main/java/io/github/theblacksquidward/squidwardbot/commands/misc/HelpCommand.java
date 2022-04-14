package io.github.theblacksquidward.squidwardbot.commands.misc;

import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGlobalCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Command
public class HelpCommand implements IGlobalCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        //TODO
        event.reply("*help*").queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Lends a helping hand.";
    }

    @Override
    public CommandData getCommandData() {
        //TODO
        return new CommandDataImpl(getName(), getDescription());
    }
}
