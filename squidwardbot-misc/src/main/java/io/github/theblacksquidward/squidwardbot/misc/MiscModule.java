package io.github.theblacksquidward.squidwardbot.misc;

import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;

@SquidwardBotModule
public class MiscModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_misc";
    }

}
