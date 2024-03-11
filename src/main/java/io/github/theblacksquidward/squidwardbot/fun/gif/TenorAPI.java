package io.github.theblacksquidward.squidwardbot.fun.gif;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.theblacksquidward.squidwardbot.fun.gif.data.GifSearch;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenorAPI {

  private static final Logger LOGGER = LoggerFactory.getLogger(TenorAPI.class);

  private static final int DEFAULT_LIMIT = 200;
  private static final String DEFAULT_MEDIA_FILTERS = "gif";

  private final OkHttpClient okHttpClient;
  private final String apiKey;

  public TenorAPI(OkHttpClient okHttpClient, String apiKey) {
    this.okHttpClient = okHttpClient;
    this.apiKey = apiKey;
  }

  private String getSearchUrl(String query) {
    return "https://tenor.googleapis.com/v2/search?key="
        + apiKey
        + "&limit="
        + DEFAULT_LIMIT
        + "&media_filter="
        + DEFAULT_MEDIA_FILTERS
        + "&q="
        + URLEncoder.encode(query, StandardCharsets.UTF_8);
  }

  public GifSearch search(String query) {
    Request request = new Request.Builder().url(getSearchUrl(query)).build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      JsonObject rootJson = JsonParser.parseString(response.body().string()).getAsJsonObject();
      return new GifSearch(rootJson);
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage());
    }
    return null;
  }
}
