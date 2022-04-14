package io.github.theblacksquidward.destinyapiwrapper;

import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class ManifestHandler {

    static final String MANIFEST_ZIP_LOCATION = "cache/manifest.zip";
    static final String UNZIPPED_MANIFEST_LOCATION = "cache/";

    /**
     * Method to download the latest manifest from the Bungie API and saves it to a manifest.zip file.
     */
    public static void downloadManifestDatabase() {
        try {
            URL url = new URL(getManifestDatabaseURL());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("X-API-Key", RequestHandler.getApiKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get the latest manifest URL from the Bungie API.
     * @return The URL location of the manifest.
     */
    public static String getManifestDatabaseURL() {
        JsonObject manifest = RequestHandler.makeAPIRequest(RequestHandler.MANIFEST_URL).getResponseAsJsonObject();
        String location = manifest.get("Response").getAsJsonObject().get("mobileWorldContentPaths").getAsJsonObject().get("en").getAsString();
        return "http://www.bungie.net" + location;
    }

    public static String getManifestJsonUrl() {
        JsonObject manifest = RequestHandler.makeAPIRequest(RequestHandler.MANIFEST_URL).getResponseAsJsonObject();
        String location = manifest.get("Response").getAsJsonObject().get("jsonWorldContentPaths").getAsJsonObject().get("en").getAsString();
        return "http://www.bungie.net" + location;
    }

    //TODO method to access the database

    //TODO method to initalize the database


}
