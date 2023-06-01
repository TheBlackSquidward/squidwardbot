package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.github.theblacksquidward.squidwardbot.audio.commands.*;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
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
    public void registerCommands(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand(new BassBoostCommand());
        commandRegistry.registerCommand(new ClearCommand());
        commandRegistry.registerCommand(new ConnectCommand());
        commandRegistry.registerCommand(new DisconnectCommand());
        commandRegistry.registerCommand(new ForcePlayCommand());
        commandRegistry.registerCommand(new LyricsCommand());
        commandRegistry.registerCommand(new NightcoreCommand());
        commandRegistry.registerCommand(new NowPlayingCommand());
        commandRegistry.registerCommand(new PauseCommand());
        commandRegistry.registerCommand(new PlayCommand());
        commandRegistry.registerCommand(new QueueCommand());
        commandRegistry.registerCommand(new RemoveCommand());
        commandRegistry.registerCommand(new RepeatCommand());
        commandRegistry.registerCommand(new SearchCommand());
        commandRegistry.registerCommand(new ShuffleCommand());
        commandRegistry.registerCommand(new SkipCommand());
        commandRegistry.registerCommand(new SlowCommand());
        commandRegistry.registerCommand(new StopCommand());
        commandRegistry.registerCommand(new UnpauseCommand());
        commandRegistry.registerCommand(new VolumeCommand());
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
