package io.github.theblacksquidward.squidwardbot.core;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);

    private static SquidwardBot instance;

    private final JDA JDA;
    private final String VERSION;
    private final Reflections REFLECTIONS;
    private final String SPOTIFY_CLIENT_ID;
    private final String SPOTIFY_CLIENT_SECRET;

    public SquidwardBot(String accessToken,
                        Reflections reflections,
                        String version,
                        String spotifyClientId,
                        String spotifyClientSecret) throws LoginException, InterruptedException {
        instance = this;
        this.REFLECTIONS = reflections;
        this.VERSION = version;
        this.SPOTIFY_CLIENT_ID = spotifyClientId;
        this.SPOTIFY_CLIENT_SECRET = spotifyClientSecret;

        //TODO VERIFY from here down
        CommandManager.captureAndRegisterCommands(REFLECTIONS);
        JDA = JDABuilder.createDefault(accessToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .addEventListeners(new CommandManager())
                .build()
                .awaitReady();
    }

    public static SquidwardBot getInstance() {
        return instance;
    }

    public String getSpotifyClientId() {
        return SPOTIFY_CLIENT_ID;
    }

    public String getSpotifyClientSecret() {
        return SPOTIFY_CLIENT_SECRET;
    }

    public String getVersion() {
        return VERSION;
    }

}