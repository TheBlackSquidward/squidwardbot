package io.github.theblacksquidward.squidwardbot;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.events.EventListenerRegistry;
import io.github.theblacksquidward.squidwardbot.database.SquidwardBotDatabase;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SquidwardBot {

    public static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBot.class);
    private static final SquidwardBot INSTANCE = null;

    private static final EventListenerRegistry eventRegistry = new EventListenerRegistry();
    private static final CommandRegistry commandRegistry = new CommandRegistry();

    private static String version;

    public static void main(String[] args) {
        Environment.getInstance().loadDotenv(args);

        final Reflections reflections = new Reflections("io.github.theblacksquidward");

        String version = SquidwardBot.class.getPackage().getImplementationVersion();
        final boolean inDev = version == null;
        if (inDev) version = "IN-DEV";
        SquidwardBot.version = version;

        SquidwardBotDatabase.init();

        CommandRegistry.captureAndRegisterCommands(reflections);
        EventListenerRegistry.captureAndRegisterEventListeners(reflections);

        LOGGER.info("Starting SquidwardBot (Version {})", version);
        final JDABuilder jdaBuilder = JDABuilder.createDefault(Environment.getInstance().getDiscordBotToken());
        eventRegistry.forEachEvent(eventListener -> {
            jdaBuilder.addEventListeners(eventListener);
            LOGGER.info("Successfully registered " + eventListener.getEventName() + " event listener.");
        });
        configureJDABuilder(jdaBuilder);
        jdaBuilder.build();
    }

    private static void configureJDABuilder(JDABuilder jdaBuilder) {
        jdaBuilder.setAudioSendFactory(new NativeAudioSendFactory());
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT);
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.ONLINE_STATUS);
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jdaBuilder.setActivity(Activity.playing("SquidwardBot | /help"));
    }

    public static SquidwardBot getInstance() {
        return INSTANCE;
    }

    public String getVersion() {
        return version;
    }

}