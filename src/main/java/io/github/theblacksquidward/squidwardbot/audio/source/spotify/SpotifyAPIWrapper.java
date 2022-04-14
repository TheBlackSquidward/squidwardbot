package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

public class SpotifyAPIWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAPIWrapper.class);

    //private final SpotifyApi spotifyApi;

    public SpotifyAPIWrapper() {

    }

    @Deprecated
    private SpotifyApi createAPI() throws IOException, ParseException, SpotifyWebApiException {
        var spotifyApi = new SpotifyApi.Builder()
                .setClientId(SquidwardBot.DOTENV.get("SPOTIFY_CLIENT_ID"))
                .setClientSecret(SquidwardBot.DOTENV.get("SPOTIFY_CLIENT_SECRET"))
                .build();
        ClientCredentialsRequest.Builder request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials credentials = request.grant_type("client_credentials").build().execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        return spotifyApi;
    }

    private SpotifyApi createSpotifyAPI() {
        return null;
    }

    // This will create a new api link and replace the old one. When this method is fired we must ensure that the pervious api has expired
    private void refreshSpotifyAPI() {

    }

    public SpotifyApi getOrRefreshAPI() {
        return null;
    }

}
