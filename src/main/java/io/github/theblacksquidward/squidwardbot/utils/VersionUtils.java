package io.github.theblacksquidward.squidwardbot.utils;

import io.github.theblacksquidward.squidwardbot.constants.Constants;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static final String VERSION_URL = "https://raw.githubusercontent.com/TheBlackSquidward/SquidwardBot/master/gradle.properties";
    private static final Pattern VERSION_PATTERN = Pattern.compile("SQUIDWARDBOT_VERSION=([0-9]+\\.[0-9]+\\.[0-9]+)");

    public static String getLatestVersion() {
        final Request request = new Request.Builder().url(VERSION_URL).build();
        try (Response response = Constants.OK_HTTP_CLIENT.newCall(request).execute()) {
            final String result = response.body().string();
            Matcher matcher = VERSION_PATTERN.matcher(result);
            if(matcher.find()) {
                return matcher.group(1);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return "ERROR";
    }

}
