package io.github.theblacksquidward.squidwardbot.commands.misc;

import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGlobalCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class PingCommand implements IGlobalCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        event.reply("The ping of the bot is " + event.getJDA().getGatewayPing() + "ms.").queue();
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Returns the ping of the bot.";
    }

}