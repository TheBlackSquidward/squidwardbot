package io.github.theblacksquidward.squidwardbot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;

@CommandController
public class PingCommand {

    @Command(value="ping")
    public void onPingCommand(CommandEvent event) {
        event.reply("Pong!");
    }

}
