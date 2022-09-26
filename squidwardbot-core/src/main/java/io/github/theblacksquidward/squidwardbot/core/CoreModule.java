package io.github.theblacksquidward.squidwardbot.core;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandManager;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.ModuleEventHandler;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@SquidwardBotModule
public class CoreModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_core";
    }

    @Override
    public void onJDABuild(JDABuilder jdaBuilder) {
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .addEventListeners(
                        new CommandManager(),
                        new ModuleEventHandler());
    }

}
