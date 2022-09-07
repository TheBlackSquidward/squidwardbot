package io.github.theblacksquidward.squidwardbot.audio;

import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;

@SquidwardBotModule
public class AudioModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_audio";
    }

    @Override
    public void onJDAReady() {
        AudioManager.init();
    }
}
