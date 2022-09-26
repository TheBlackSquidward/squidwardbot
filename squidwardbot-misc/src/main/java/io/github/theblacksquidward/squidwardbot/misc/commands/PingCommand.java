package io.github.theblacksquidward.squidwardbot.misc.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class PingCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        event.getHook().sendMessage("Gateway Ping: " + event.getJDA().getGatewayPing() + "ms").queue();
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Gets the ping of the bot.";
    }

}
