package io.github.theblacksquidward.squidwardbot.core;

import io.github.theblacksquidward.squidwardbot.commands.CommandManager;
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
    private static final Reflections REFLECTIONS = new Reflections("io.github.theblacksquidward");

    private static SquidwardBot instance;

    private final JDA jda;
    private final String version;

    public SquidwardBot(String accessToken,
                        String version) throws LoginException, InterruptedException{
        instance = this;
        this.version = version;
        //TODO VERIFY from here down
        CommandManager.captureAndRegisterCommands(REFLECTIONS);
        jda = JDABuilder.createDefault(accessToken)
                .enableCache(CacheFlag.VOICE_STATE)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.playing("SquidwardBot | /help"))
                .addEventListeners(new CommandManager())
                .build()
                .awaitReady();

        //TODO based on if its in dev or not
        registerDevCommands(jda);
    }

    //TODO to move
    private static void initializeAllCommands(CommandListUpdateAction commandListUpdateAction) {
        //TODO global commands arent registered globally
        CommandManager.getAllCommands().forEach((string, cmd) -> commandListUpdateAction.addCommands(cmd.getCommandData()));
    }

    //TODO to move
    private static void registerDevCommands(@NotNull JDA jdaInstance) {
        final Guild GUILD_1 = jdaInstance.getGuildById(488101404364505120L);
        final CommandListUpdateAction GUILD_1_COMMANDS = GUILD_1.updateCommands();
        initializeAllCommands(GUILD_1_COMMANDS);
        GUILD_1_COMMANDS.queue();
    }

    private static void registerGlobalCommands() {

    }

}
