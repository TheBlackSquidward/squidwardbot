package io.github.theblacksquidward.squidwardbot.core.modules;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ModuleEventHandler extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ModuleRegistry.getInstance().forEachPlugin(squidwardBotModule -> squidwardBotModule.onJDAReady(event));
    }

}
