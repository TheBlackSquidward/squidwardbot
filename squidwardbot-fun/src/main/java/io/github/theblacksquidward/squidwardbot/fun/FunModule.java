package io.github.theblacksquidward.squidwardbot.fun;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.fun.commands.CuddleCommand;
import io.github.theblacksquidward.squidwardbot.fun.commands.KissCommand;

@SquidwardBotModule
public class FunModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_fun";
    }

    @Override
    public void registerCommands(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand(new CuddleCommand());
        commandRegistry.registerCommand(new KissCommand());
    }

}
