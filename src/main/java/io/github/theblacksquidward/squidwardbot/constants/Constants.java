package io.github.theblacksquidward.squidwardbot.constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.GLA;
import io.github.theblacksquidward.squidwardbot.core.ShutdownHooks;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

public class Constants {

    public static final Logger LOGGER = LoggerFactory.getLogger("SquidwardBot");

    public static final Random RANDOM = new Random();
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    public static final GLA GENIUS_LYRICS_API = new GLA();

    static {
        ShutdownHooks.register(() -> {
            OK_HTTP_CLIENT.dispatcher().executorService().shutdown();
            OK_HTTP_CLIENT.connectionPool().evictAll();
            try {
                OK_HTTP_CLIENT.cache().close();
            } catch (IOException | NullPointerException exception) {
                LOGGER.error("Failed to close OkHttpClient cache.", exception);
            }
        });
    }

}
