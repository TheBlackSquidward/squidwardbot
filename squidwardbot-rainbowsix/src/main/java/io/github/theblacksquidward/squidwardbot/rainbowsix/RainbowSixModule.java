package io.github.theblacksquidward.squidwardbot.rainbowsix;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.rainbowsix.commands.StatsCommand;

@SquidwardBotModule
public class RainbowSixModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_rainbowsix";
    }

    @Override
    public void registerCommands(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand(new StatsCommand());
    }

}
