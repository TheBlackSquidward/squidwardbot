package io.github.theblacksquidward.squidwardbot.core;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ModuleRegistry;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);

    private final CommandRegistry commandRegistry = new CommandRegistry();

    private static SquidwardBot instance;

    private final boolean inDev;
    private final String version;
    private final Dotenv dotenv;

    public SquidwardBot(boolean inDev,
                        Dotenv dotenv,
                        Reflections reflections,
                        String version) throws InterruptedException {
        instance = this;
        this.inDev = inDev;
        this.dotenv = dotenv;
        this.version = version;
        ModuleRegistry.getInstance().captureAndInitModules(reflections);
        ModuleRegistry.getInstance().forEachPlugin(module -> module.registerCommands(commandRegistry));
        final JDABuilder jdaBuilder = JDABuilder.createDefault(
                inDev ? dotenv.get("DISCORD_BOT_DEV_TOKEN") : dotenv.get("DISCORD_BOT_TOKEN")
        );
        ModuleRegistry.getInstance().forEachPlugin(module -> module.onJDABuild(jdaBuilder));
        jdaBuilder.build().awaitReady();
    }

    public static SquidwardBot getInstance() {
        return instance;
    }

    public String getSpotifyClientId() {
        return dotenv.get("SPOTIFY_CLIENT_ID");
    }

    public String getSpotifyClientSecret() {
        return dotenv.get("SPOTIFY_CLIENT_SECRET");
    }

    public String getAppleMusicMediaApiToken() {
        return dotenv.get("APPLE_MUSIC_MEDIA_API_TOKEN");
    }

    public String getDeezerMasterDecryptionKey() {
        return dotenv.get("DEEZER_MASTER_DECRYPTION_KEY");
    }

    public String getTenorApiKey() {
        return dotenv.get("TENOR_API_KEY");
    }

    public boolean isInDev() {
        return inDev;
    }

    public String getVersion() {
        return version;
    }

    public Long getOwnerId() {
        return Long.parseLong(dotenv.get("OWNER_ID"));
    }
}