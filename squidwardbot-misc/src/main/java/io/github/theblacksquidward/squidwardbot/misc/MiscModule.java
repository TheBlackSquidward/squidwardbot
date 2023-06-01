package io.github.theblacksquidward.squidwardbot.misc;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.misc.commands.*;
import io.github.theblacksquidward.squidwardbot.misc.events.WeebsEventHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@SquidwardBotModule
public class MiscModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_misc";
    }

    @Override
    public void registerCommands(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand(new BotInfoCommand());
        commandRegistry.registerCommand(new PingCommand());
        commandRegistry.registerCommand(new ServerInfoCommand());
        commandRegistry.registerCommand(new ShutdownCommand());
        commandRegistry.registerCommand(new UptimeCommand());
        commandRegistry.registerCommand(new UserInfoCommand());
        commandRegistry.registerCommand(new VersionCommand());
    }

    @Override
    public void onJDABuild(JDABuilder jdaBuilder) {
        jdaBuilder.enableCache(CacheFlag.ONLINE_STATUS);
        jdaBuilder.addEventListeners(new WeebsEventHandler());
    }

}
