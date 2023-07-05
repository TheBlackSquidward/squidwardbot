package io.github.theblacksquidward.squidwardbot;

import com.google.common.base.Stopwatch;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.theblacksquidward.squidwardbot.utils.StringUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private static final Environment INSTANCE = new Environment();
    private Dotenv env;

    public static Environment getInstance() {
        return INSTANCE;
    }

    public void loadDotenv(String[] args) {
        if (this.env != null)
            throw new IllegalStateException("Environment already loaded!");
        LOGGER.info("Loading environment...");
        final Stopwatch timer = Stopwatch.createStarted();
        OptionParser optionParser = new OptionParser();
        OptionSpec<String> envFilePathOptionSpec = optionParser.accepts("env").withRequiredArg().required().ofType(String.class);
        OptionSet optionSet = optionParser.parse(args);
        String filePath = envFilePathOptionSpec.value(optionSet);
        this.env = Dotenv.configure().directory(filePath).load();
        LOGGER.debug("Loaded environment variables: {}", StringUtils.getIndentedStringList(getEnvironmnetKeys()));
        timer.stop();
        LOGGER.info("Successfully loaded environment in {}. Successfully loaded {} environment variables.", timer.elapsed().toMillis(), getEnvironmentEntryCount());
    }

    public void printEnvironment() {
        this.env.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE)
                .forEach(entry -> LOGGER.debug(entry.getKey() + "=" + entry.getValue()));
    }

    @UnmodifiableView
    public Set<String> getEnvironmnetKeys() {
        return env.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).stream().map(DotenvEntry::getKey).collect(Collectors.toUnmodifiableSet());
    }

    @UnmodifiableView
    public Set<String> getEnvironmnetValues() {
        return env.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).stream().map(DotenvEntry::getValue).collect(Collectors.toUnmodifiableSet());
    }

    public int getEnvironmentEntryCount() {
        return env.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).size();
    }

    public boolean getBoolean(String key) throws IllegalStateException {
        try {
            return Boolean.parseBoolean(getString(key));
        } catch (final NumberFormatException exception) {
            throw new IllegalStateException("'" + key + "' is not an boolean!", exception);
        }
    }

    public double getDouble(String key) throws IllegalStateException {
        try {
            return Double.parseDouble(getString(key));
        } catch (final NumberFormatException exception) {
            throw new IllegalStateException("'" + key + "' is not an double!", exception);
        }
    }

    public float getFloat(String key) throws IllegalStateException {
        try {
            return Float.parseFloat(getString(key));
        } catch (final NumberFormatException exception) {
            throw new IllegalStateException("'" + key + "' is not an float!", exception);
        }
    }

    public int getInteger(String key) throws IllegalStateException {
        try {
            return Integer.parseInt(getString(key));
        } catch (final NumberFormatException exception) {
            throw new IllegalStateException("'" + key + "' is not an integer!", exception);
        }
    }

    public long getLong(String key) throws IllegalStateException {
        try {
            return Long.parseLong(getString(key));
        } catch (final NumberFormatException exception) {
            throw new IllegalStateException("'" + key + "' is not an long!", exception);
        }
    }

    public String getString(String key) throws IllegalStateException {
        try {
            return env.get(key);
        } catch (final NullPointerException exception) {
            throw new IllegalStateException("'" + key + "' does not exist in this .env!", exception);
        }
    }

    public String getDiscordBotToken() {
        return getString("DISCORD_BOT_TOKEN");
    }

    public long getOwnerId() {
        return getLong("OWNER_ID");
    }

    public String getMongoDBUsername() {
        return getString("MONGODB_USERNAME");
    }

    public String getMongoDBPassword() {
        return getString("MONGODB_PASSWORD");
    }

    public String getSpotifyClientId() {
        return getString("SPOTIFY_CLIENT_ID");
    }

    public String getSpotifyClientSecret() {
        return getString("SPOTIFY_CLIENT_SECRET");
    }

    public String getAppleMusicMediaApiToken() {
        return getString("APPLE_MUSIC_MEDIA_API_TOKEN");
    }

    public String getDeezerMasterDecryptionKey() {
        return getString("DEEZER_MASTER_DECRYPTION_KEY");
    }

    public String getTenorApiKey() {
        return getString("TENOR_API_KEY");
    }

    //TODO: rename
    public long getHarryServerId() {
        return getLong("HARRY_SERVER_ID");
    }

    public long getWeebsServerId() {
        return getLong("WEEBS_SERVER_ID");
    }

    public long getGlobalMemberUpdateChannelId() {
        return getLong("GLOBAL_MEMBER_UPDATE_CHANNEL_ID");
    }

    public long getGlobalVCUpdateChannelId() {
        return getLong("GLOBAL_VC_UPDATE_CHANNEL_ID");
    }

    public long getGlobalMessageUpdateChannelId() {
        return getLong("GLOBAL_MESSAGE_UPDATE_CHANNEL_ID");
    }

    public long getGlobalChannelUpdateChannelId() {
        return getLong("GLOBAL_CHANNEL_UPDATE_CHANNEL_ID");
    }

    public long getGlobalRoleUpdateChannelId() {
        return getLong("GLOBAL_ROLE_UPDATE_CHANNEL_ID");
    }

}
