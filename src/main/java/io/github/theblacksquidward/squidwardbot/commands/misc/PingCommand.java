package io.github.theblacksquidward.squidwardbot.commands.misc;

import io.github.theblacksquidward.squidwardbot.commands.IGlobalCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand implements IGlobalCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Pong!";
    }

}
