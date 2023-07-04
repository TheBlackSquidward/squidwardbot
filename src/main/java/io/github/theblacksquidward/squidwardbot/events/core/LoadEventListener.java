package io.github.theblacksquidward.squidwardbot.events.core;

import io.github.theblacksquidward.squidwardbot.constants.Constants;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public class LoadEventListener extends SquidwardBotEventListener {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Constants.LOGGER.info("Successfully loaded SquidwardBot!");
    }

}
