package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class AppleMusicAPI {

    public static final String COUNTRY_CODE = "gb";

    private static final Logger LOGGER = LoggerFactory.getLogger(AppleMusicAPI.class);

    private final HttpInterfaceManager httpInterfaceManager;

    private String currentToken;
    private Instant oldToken;

    public AppleMusicAPI(HttpInterfaceManager httpInterfaceManager) {
        this.httpInterfaceManager = httpInterfaceManager;
    }

    public String getCountryCode() {
        return COUNTRY_CODE;
    }

    private String requestToken() throws IOException {
        HttpGet httpGetRequest = new HttpGet("");
        try(CloseableHttpResponse response = httpInterfaceManager.getInterface().execute(httpGetRequest)) {
            Document document = Jsoup.parse(response.getEntity().getContent(), null, "");
            return JsonBrowser.parse(URLDecoder.decode(document.selectFirst("meta[name=desktop-music-app/config/environment]").attr("content"), StandardCharsets.UTF_8)).get("MEDIA_API").get("token").text();
        }
    }

    private String getAndRefreshToken() throws IOException {
        if(currentToken == null || oldToken == null || oldToken.isBefore(Instant.now())) {
            currentToken = requestToken();
            oldToken = Instant.ofEpochSecond(JsonBrowser.parse(new String(Base64.getDecoder().decode(currentToken.split("\\.")[1]))).get("exp").asLong(0));
        }
        return currentToken;
    }

}
