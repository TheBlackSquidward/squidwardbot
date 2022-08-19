package io.github.theblacksquidward.squidwardbot.core;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);

    private static SquidwardBot instance;
    private static Reflections reflections;
    private static String spotifyClientId;
    private static String spotifyClientSecret;

    private final JDA jda;
    private final String version;

    public SquidwardBot(String accessToken,
                        Reflections reflections,
                        String version,
                        String spotifyClientId,
                        String spotifyClientSecret) throws LoginException, InterruptedException{
        instance = this;
        this.reflections = reflections;
        this.version = version;
        this.spotifyClientId = spotifyClientId;
        this.spotifyClientSecret = spotifyClientSecret;

        //TODO VERIFY from here down
        CommandManager.captureAndRegisterCommands(reflections);
        jda = JDABuilder.createDefault(accessToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .addEventListeners(new CommandManager())
                .build()
                .awaitReady();
    }

    private static void registerGlobalCommands() {

    }

    public static String getSpotifyClientId() {
        return spotifyClientId;
    }

    public static String getSpotifyClientSecret() {
        return spotifyClientSecret;
    }

    public static SquidwardBotBuilder builder() {
        return new SquidwardBotBuilder();
    }
}
