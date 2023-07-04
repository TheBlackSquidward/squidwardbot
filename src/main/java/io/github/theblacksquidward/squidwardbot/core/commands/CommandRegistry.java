package io.github.theblacksquidward.squidwardbot.core.commands;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import io.github.theblacksquidward.squidwardbot.utils.StringUtils;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

@EventListener
public class CommandRegistry extends SquidwardBotEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);
    private static final Map<String, SquidwardBotCommand> COMMANDS = Maps.newHashMap();

    public static void captureAndRegisterCommands(Reflections reflections) {
        LOGGER.info("Beginning to scan for commands...");
        final Stopwatch timer = Stopwatch.createStarted();
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Command.class);
        LOGGER.info("Successfully found " + annotatedClasses.size() + " commands, Attempting to register them...");
        annotatedClasses.forEach((annotatedClass) -> {
            try {
                SquidwardBotCommand squidwardBotCommand = (SquidwardBotCommand) annotatedClass.getDeclaredConstructor().newInstance();
                registerCommand(squidwardBotCommand);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                //TODO log better
                e.printStackTrace();
            }
        });
        LOGGER.debug("Loaded commands: {}", StringUtils.getIndentedStringList(getCommands()));
        timer.stop();
        LOGGER.info("Finished capturing and registering commands in {}. Successfully registered {} commands.", timer, getCommands().size());
    }

    public static void registerCommand(SquidwardBotCommand command) {
        COMMANDS.putIfAbsent(command.getName(), command);
        LOGGER.info("Successfully registered " + command.getName() + " command.");
    }

    @NotNull
    @UnmodifiableView
    public static Set<SquidwardBotCommand> getCommands() {
        return Set.copyOf(COMMANDS.values());
    }

    public static int getCommandsSize() {
        return COMMANDS.size();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        event.getJDA().getGuilds().forEach(guild -> {
            final CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
            getCommands().forEach(cmd -> commandListUpdateAction.addCommands(cmd.getCommandData()));
            commandListUpdateAction.queue();
        });
        getCommands().forEach(event.getJDA()::addEventListener);
    }


}
