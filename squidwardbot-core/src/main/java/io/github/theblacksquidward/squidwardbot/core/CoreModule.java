package io.github.theblacksquidward.squidwardbot.core;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.core.constants.Constants;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.ModuleEventHandler;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

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
                        new ModuleEventHandler());
    }

    @Override
    public void onJDAReady(ReadyEvent event) {
        Set<SquidwardBotCommand> commands = CommandRegistry.getCommands();
        event.getJDA().getGuilds().forEach(guild -> {
            final CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
            commands.forEach(cmd -> commandListUpdateAction.addCommands(cmd.getCommandData()));
            commandListUpdateAction.queue();
        });
        commands.forEach(event.getJDA()::addEventListener);
    }

    @Override
    public void onJDAShutdown(ShutdownEvent event) {
        OkHttpClient okHttpClient = Constants.OK_HTTP_CLIENT;
        Logger LOGGER = LoggerFactory.getLogger("OKHttpClient");
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
        try {
            okHttpClient.cache().close();
        } catch (IOException | NullPointerException exception) {
            LOGGER.error("Could not successfully shutdown OkHttpClient.");
        }
    }

}
