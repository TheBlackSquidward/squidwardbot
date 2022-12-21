package io.github.theblacksquidward.squidwardbot.core.constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.GLA;
import okhttp3.OkHttpClient;

import java.util.Random;

public class Constants {

    public static final Random RANDOM = new Random();
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    public static final GLA GENIUS_LYRICS_API = new GLA();

}
