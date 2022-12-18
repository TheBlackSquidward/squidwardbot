package io.github.theblacksquidward.squidwardbot.core;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandManager;
import io.github.theblacksquidward.squidwardbot.core.modules.ModuleRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);

    private static SquidwardBot instance;

    private final JDA JDA;
    private final String VERSION;
    private final Reflections REFLECTIONS;
    private final String SPOTIFY_CLIENT_ID;
    private final String SPOTIFY_CLIENT_SECRET;
    private final String DEEZER_MASTER_DECRYPTION_KEY;
    private final Long OWNER_ID;

    public SquidwardBot(Dotenv dotenv,
                        Reflections reflections,
                        String version) throws InterruptedException {
        instance = this;
        this.REFLECTIONS = reflections;
        this.VERSION = version;
        this.SPOTIFY_CLIENT_ID = dotenv.get("SPOTIFY_CLIENT_ID");
        this.SPOTIFY_CLIENT_SECRET = dotenv.get("SPOTIFY_CLIENT_SECRET");
        this.DEEZER_MASTER_DECRYPTION_KEY = dotenv.get("DEEZER_MASTER_DECRYPTION_KEY");
        this.OWNER_ID = Long.parseLong(dotenv.get("OWNER_ID"));
        ModuleRegistry.getInstance().captureAndInitModules(reflections);
        CommandManager.captureAndRegisterCommands(REFLECTIONS);
        final JDABuilder jdaBuilder = JDABuilder.createDefault(dotenv.get("DISCORD_BOT_TOKEN"));
        ModuleRegistry.getInstance().forEachPlugin(module -> module.onJDABuild(jdaBuilder));
        JDA = jdaBuilder.build().awaitReady();
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

    public String getDeezerMasterDecryptionKey() {
        return DEEZER_MASTER_DECRYPTION_KEY;
    }

    public String getVersion() {
        return VERSION;
    }

    public Long getOwnerId() {
        return OWNER_ID;
    }
}