package io.github.theblacksquidward.squidwardbot.core;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ModuleRegistry;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);

    private final CommandRegistry commandRegistry = new CommandRegistry();

    private static SquidwardBot instance = null;

    private final String VERSION;
    private final Dotenv DOTENV;

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public SquidwardBot(boolean inDev,
                        Dotenv dotenv,
                        Reflections reflections,
                        String version) throws InterruptedException, SQLException {
        instance = this;
        this.DOTENV = dotenv;
        this.VERSION = version;
        this.mongoClient = MongoClients.create("mongodb+srv://" + dotenv.get("MONGODB_USERNAME") + ":" + dotenv.get("MONGODB_PASSWORD") + "@squidwardbot.nd0ncel.mongodb.net/?retryWrites=true&w=majority");
        this.mongoDatabase = mongoClient.getDatabase("squidwardbot");
        ModuleRegistry.getInstance().captureAndInitModules(reflections);
        ModuleRegistry.getInstance().forEachPlugin(module -> module.registerCommands(commandRegistry));
        final JDABuilder jdaBuilder = JDABuilder.createDefault(dotenv.get("DISCORD_BOT_TOKEN"));
        ModuleRegistry.getInstance().forEachPlugin(module -> module.onJDABuild(jdaBuilder));
        jdaBuilder.build().awaitReady();
    }

    public static SquidwardBot getInstance() {
        if (instance == null) throw new RuntimeException("The instance of SquidwardBot does not exist!");
        return instance;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
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

    // TODO: reaname
    public Long getGlobalLoggingServerId() {
        return Long.parseLong(DOTENV.get("HARRY_SERVER_ID"));
    }

    public Long getWeebsServerId() {
        return Long.parseLong(DOTENV.get("WEEBS_SERVER_ID"));
    }

    public Long getRoleUpdateChannelId() {
        return Long.parseLong(DOTENV.get("LOGGING_ROLE_CHANNEL_ID"));
    }

    public Long getMemberUpdateChannelId() {
        return Long.parseLong(DOTENV.get("MEMBER_UPDATE_CHANNEL_ID"));
    }

    public Long getVoiceUpdateChannelId() {
        return Long.parseLong(DOTENV.get("VC_UPDATE_CHANNEL_ID"));
    }

    public Long getChannelUpdateChannelId() {
        return Long.parseLong(DOTENV.get("CHANNEL_UPDATE_CHANNEL_ID"));
    }

    public Long getMessageUpdateChannelId() {
        return Long.parseLong(DOTENV.get("MESSAGE_UPDATE_CHANNEL_ID"));
    }

}