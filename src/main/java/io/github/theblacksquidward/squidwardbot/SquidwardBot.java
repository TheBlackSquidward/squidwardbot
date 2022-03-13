package io.github.theblacksquidward.squidwardbot;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.audio.GuildAudioManager;
import io.github.theblacksquidward.squidwardbot.commands.CommandHandler;
import io.github.theblacksquidward.squidwardbot.commands.misc.MusicCommand;
import io.github.theblacksquidward.squidwardbot.commands.misc.PingCommand;
import io.github.theblacksquidward.squidwardbot.commands.music.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
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
    private static final Reflections REFLECTIONS = new Reflections("io.github.theblacksquidward.squidwardbot");

    private static SpotifyApi SPOTIFY_API;

    private static GuildAudioManager GUILD_AUDIO_MANAGER;

    public static void main(String[] args) throws LoginException, InterruptedException {
        //TODO make this not hard coded
        String version = "1.0-SNAPSHOT";
        LOGGER.info("Starting SquidwardBot v{}", version);
        JDA JDA = JDABuilder.createDefault(DOTENV.get("DISCORD_BOT_TOKEN"))
                .enableCache(CacheFlag.VOICE_STATE)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .addEventListeners(new CommandHandler())
                .build()
                .awaitReady();

        try {
            SPOTIFY_API = initializeSpotifyAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GUILD_AUDIO_MANAGER = new GuildAudioManager();

        final Guild GUILD = JDA.getGuildById(488101404364505120L);
        final CommandListUpdateAction GUILD_COMMANDS = GUILD.updateCommands();
        initalizeCommands(GUILD_COMMANDS);
        GUILD_COMMANDS.queue();
    }

    public static GuildAudioManager getGuildAudioManager() {
        return GUILD_AUDIO_MANAGER;
    }

    public static SpotifyApi getSpotifyAPI() {
        return SPOTIFY_API;
    }

    @NotNull
    private static SpotifyApi initializeSpotifyAPI() throws IOException, ParseException, SpotifyWebApiException {
        var spotifyApi = new SpotifyApi.Builder()
                .setClientId(DOTENV.get("SPOTIFY_CLIENT_ID"))
                .setClientSecret(DOTENV.get("SPOTIFY_CLIENT_SECRET"))
                .build();
        ClientCredentialsRequest.Builder request = new ClientCredentialsRequest.Builder(spotifyApi.getClientId(), spotifyApi.getClientSecret());
        ClientCredentials credentials = request.grant_type("client_credentials").build().execute();
        spotifyApi.setAccessToken(credentials.getAccessToken());
        return spotifyApi;
    }

    private static void initalizeCommands(CommandListUpdateAction commandListUpdateAction) {
        CommandHandler.registerGuildCommands(
                new MusicCommand(),
                new ClearCommand(),
                new ConnectCommand(),
                new DisconnectCommand(),
                new PlayCommand(),
                new SkipCommand());
        CommandHandler.registerGlobalCommands(new PingCommand());
        //TODO global commands arent registered globally
        CommandHandler.getAllCommands().forEach((string, cmd) -> commandListUpdateAction.addCommands(cmd.getCommandData()));
    }

}
