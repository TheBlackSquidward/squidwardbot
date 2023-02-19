package io.github.theblacksquidward.squidwardbot.core;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ModuleRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);

    private final CommandRegistry commandRegistry = new CommandRegistry();

    private static SquidwardBot instance;

    private final JDA JDA;
    private final String VERSION;
    private final Dotenv DOTENV;

    public SquidwardBot(Dotenv dotenv,
                        Reflections reflections,
                        String version) throws InterruptedException {
        instance = this;
        this.DOTENV = dotenv;
        this.VERSION = version;
        ModuleRegistry.getInstance().captureAndInitModules(reflections);
        ModuleRegistry.getInstance().forEachPlugin(module -> module.registerCommands(commandRegistry));
        final JDABuilder jdaBuilder = JDABuilder.createDefault(dotenv.get("DISCORD_BOT_TOKEN"));
        ModuleRegistry.getInstance().forEachPlugin(module -> module.onJDABuild(jdaBuilder));
        JDA = jdaBuilder.build().awaitReady();
    }

    public static SquidwardBot getInstance() {
        return instance;
    }

    public String getSpotifyClientId() {
        return DOTENV.get("SPOTIFY_CLIENT_ID");
    }

    public String getSpotifyClientSecret() {
        return DOTENV.get("SPOTIFY_CLIENT_SECRET");
    }

    public String getAppleMusicMediaApiToken() {
        return DOTENV.get("APPLE_MUSIC_MEDIA_API_TOKEN");
    }

    public String getDeezerMasterDecryptionKey() {
        return DOTENV.get("DEEZER_MASTER_DECRYPTION_KEY");
    }

    public String getTenorApiKey() {
        return DOTENV.get("TENOR_API_KEY");
    }

    public String getVersion() {
        return VERSION;
    }

    public Long getOwnerId() {
        return Long.parseLong(DOTENV.get("OWNER_ID"));
    }
}