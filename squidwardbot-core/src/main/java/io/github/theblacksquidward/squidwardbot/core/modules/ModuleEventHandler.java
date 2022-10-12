package io.github.theblacksquidward.squidwardbot.core.modules;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ModuleEventHandler extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ModuleRegistry.getInstance().forEachPlugin(squidwardBotModule -> squidwardBotModule.onJDAReady(event));
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        ModuleRegistry.getInstance().forEachPlugin(squidwardBotModule -> squidwardBotModule.onJDAShutdown(event));
    }

}
