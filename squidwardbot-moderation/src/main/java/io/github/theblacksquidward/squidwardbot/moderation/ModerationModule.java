package io.github.theblacksquidward.squidwardbot.moderation;

import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;

@SquidwardBotModule
public class ModerationModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_moderation";
    }

}
