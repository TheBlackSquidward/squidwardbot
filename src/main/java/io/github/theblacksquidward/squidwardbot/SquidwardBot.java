package io.github.theblacksquidward.squidwardbot;

import com.github.kaktushose.jda.commands.JDACommands;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);
    private static final Dotenv DOTENV = Dotenv.load();

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
        AUDIO_MANAGER = new AudioManager();
    }

}
