package io.github.theblacksquidward.squidwardbot.moderation;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.moderation.commands.DeafeanCommand;
import io.github.theblacksquidward.squidwardbot.moderation.commands.UndeafenCommand;
import io.github.theblacksquidward.squidwardbot.moderation.commands.UnnickCommand;

@SquidwardBotModule
public class ModerationModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_moderation";
    }

    @Override
    public void registerCommands(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand(new DeafeanCommand());
        commandRegistry.registerCommand(new UndeafenCommand());
        commandRegistry.registerCommand(new UnnickCommand());
    }

}
