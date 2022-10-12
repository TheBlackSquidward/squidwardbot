package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;

@SquidwardBotModule
public class AudioModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_audio";
    }

    @Override
    public void onJDABuild(JDABuilder jdaBuilder) {
        jdaBuilder.setAudioSendFactory(new NativeAudioSendFactory());
    }

    @Override
    public void onJDAReady(ReadyEvent event) {
        AudioManager.init();
    }

}
