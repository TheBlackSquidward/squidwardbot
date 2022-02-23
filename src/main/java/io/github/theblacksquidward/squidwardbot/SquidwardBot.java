package io.github.theblacksquidward.squidwardbot;

import com.github.kaktushose.jda.commands.JDACommands;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);
    private static final Dotenv DOTENV = Dotenv.load();

    public static SpotifyApi SPOTIFY_API;

    public static AudioManager AUDIO_MANAGER;

    public static void main(String[] args) throws LoginException {
        //TODO make this not hard coded
        String version = "1.0-SNAPSHOT";
        LOGGER.info("Starting SquidwardBot v{}", version);
        JDA jda = JDABuilder.createDefault(DOTENV.get("DISCORD_BOT_TOKEN"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .build();
        JDACommands jdaCommands = JDACommands.start(jda, SquidwardBot.class);

        try {
            SPOTIFY_API = initializeSpotify();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AUDIO_MANAGER = new AudioManager();
    }

    private static SpotifyApi initializeSpotify() throws IOException, ParseException, SpotifyWebApiException {
        var spotifyApi = new SpotifyApi.Builder()
                .setClientId(DOTENV.get("SPOTIFY_CLIENT_ID"))
                .setClientSecret(DOTENV.get("SPOTIFY_CLIENT_SECRET"))
                .build();
        ClientCredentialsRequest.Builder request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials credentials = request.grant_type("client_credentials").build().execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        return spotifyApi;
    }

}
