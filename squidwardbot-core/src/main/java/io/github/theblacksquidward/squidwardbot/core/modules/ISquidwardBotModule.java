package io.github.theblacksquidward.squidwardbot.core.modules;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;

public interface ISquidwardBotModule {

    String getModuleIdentifier();

    default void onModuleRegister() {}

    default void onJDABuild(JDABuilder jdaBuilder) {}

    default void onJDAReady(ReadyEvent event) {}

}
