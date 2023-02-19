package io.github.theblacksquidward.squidwardbot.core.modules;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;

public interface ISquidwardBotModule {

    String getModuleIdentifier();

    default void onModuleInit() {}
    default void onModuleReload() {}

    default void registerCommands(CommandRegistry commandRegistry) {}

    default void onJDABuild(JDABuilder jdaBuilder) {}
    default void onJDAReady(ReadyEvent event) {}
    default void onJDAShutdown(ShutdownEvent event) {}

}
