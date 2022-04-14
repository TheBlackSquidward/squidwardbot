package io.github.theblacksquidward.squidwardbot;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.audio.GuildAudioManager;
import io.github.theblacksquidward.squidwardbot.commands.CommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);
    public static final Dotenv DOTENV = Dotenv.load();
    private static final Reflections REFLECTIONS = new Reflections("io.github.theblacksquidward.squidwardbot");

    private static GuildAudioManager GUILD_AUDIO_MANAGER;

    public static void main(String[] args) throws LoginException, InterruptedException {
        //TODO make this not hard coded
        String version = "1.0-SNAPSHOT";
        LOGGER.info("Starting SquidwardBot v{}", version);
        CommandHandler.captureAndRegisterCommands(REFLECTIONS);
        JDA JDA = JDABuilder.createDefault(DOTENV.get("DISCORD_BOT_TOKEN"))
                .enableCache(CacheFlag.VOICE_STATE)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .addEventListeners(new CommandHandler())
                .build()
                .awaitReady();

        GUILD_AUDIO_MANAGER = new GuildAudioManager();

        //TODO based on if its in dev or not
        registerDevCommands(JDA);
    }

    public static GuildAudioManager getGuildAudioManager() {
        return GUILD_AUDIO_MANAGER;
    }

    //TODO to move
    private static void initializeAllCommands(CommandListUpdateAction commandListUpdateAction) {
        //TODO global commands arent registered globally
        CommandHandler.getAllCommands().forEach((string, cmd) -> commandListUpdateAction.addCommands(cmd.getCommandData()));
    }

    //TODO to move
    private static void registerDevCommands(@NotNull JDA jdaInstance) {
        final Guild GUILD_1 = jdaInstance.getGuildById(488101404364505120L);
        final Guild GUILD_2 = jdaInstance.getGuildById(890699214391238707L);
        final CommandListUpdateAction GUILD_1_COMMANDS = GUILD_1.updateCommands();
        final CommandListUpdateAction GUILD_2_COMMANDS = GUILD_2.updateCommands();
        initializeAllCommands(GUILD_1_COMMANDS);
        initializeAllCommands(GUILD_2_COMMANDS);
        GUILD_1_COMMANDS.queue();
        GUILD_2_COMMANDS.queue();
    }

    private static void registerGlobalCommands() {

    }

}
