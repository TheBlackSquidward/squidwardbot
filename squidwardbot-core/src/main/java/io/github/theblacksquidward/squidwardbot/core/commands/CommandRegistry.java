package io.github.theblacksquidward.squidwardbot.core.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);
    private static final Map<String, SquidwardBotCommand> COMMANDS = new HashMap<>();

    public void registerCommand(SquidwardBotCommand command) {
        COMMANDS.putIfAbsent(command.getName(), command);
        LOGGER.info("Successfully registered " + command.getName() + " command.");
    }

    @NotNull
    @UnmodifiableView
    public static Set<SquidwardBotCommand> getCommands() {
        return Set.copyOf(COMMANDS.values());
    }

    public static int getCommandsSize() {
        return COMMANDS.size();
    }


}
