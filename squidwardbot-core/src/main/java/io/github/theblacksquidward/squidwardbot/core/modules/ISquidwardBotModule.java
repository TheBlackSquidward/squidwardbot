package io.github.theblacksquidward.squidwardbot.core.modules;

public interface ISquidwardBotModule {

    String getModuleIdentifier();

    default void onModuleRegister() {}

    default void onJDAReady() {}

}
