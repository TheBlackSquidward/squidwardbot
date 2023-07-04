package io.github.theblacksquidward.squidwardbot.events.core;

import io.github.theblacksquidward.squidwardbot.constants.Constants;
import io.github.theblacksquidward.squidwardbot.core.ShutdownHooks;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public class ShutdownEventListener extends SquidwardBotEventListener {

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        Constants.LOGGER.info("Shutting down....");
        ShutdownHooks.runShutdownHooks();
    }

}
