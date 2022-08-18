package io.github.theblacksquidward.squidwardbot.audio.source.spotify;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

public class SpotifyAPIOLD {

    private final SpotifyApi spotifyApi;

    private SpotifyAPIOLD() throws IOException, ParseException, SpotifyWebApiException {
        this.spotifyApi = initializeSpotifyAPI();
    }

    public SpotifyApi getSpotifyAPI() {
        return spotifyApi;
    }

    @NotNull
    private static SpotifyApi initializeSpotifyAPI() throws IOException, ParseException, SpotifyWebApiException {
        var spotifyApi = new SpotifyApi.Builder()
                .setClientId(SquidwardBot.getSpotifyClientId())
                .setClientSecret(SquidwardBot.getSpotifyClientSecret())
                .build();
        ClientCredentialsRequest.Builder request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials credentials = request.grant_type("client_credentials").build().execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        return spotifyApi;
    }

    @Nullable
    public static SpotifyAPIOLD createSpotifyAPI() {
        try {
            return new SpotifyAPIOLD();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
        return null;
    }

}
