package io.github.theblacksquidward.destinyapiwrapper;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class RequestHandler {

    public static final String BASE_URL = "https://www.bungie.net/platform/Destiny2/";
    public static final String MANIFEST_URL = BASE_URL + "Manifest/";

    /**
     * Method to make a request to the Bungie API.
     * @param url The url of the request.
     * @return Returns a new {@link RequestResponse}
     */
    public static RequestResponse makeAPIRequest(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(url)).timeout(Duration.ofMinutes(3));
        builder.setHeader("X-API-Key", getApiKey());
        return new RequestResponse(builder.build());
    }

    /**
     * Method used to get the private API key used to access the Bungie API
     * @return Secret API Key used as a header for an API Request
     */
    public static String getApiKey() {
        return FileUtils.INSTANCE.getDestinyAPIKey();
    }

}
