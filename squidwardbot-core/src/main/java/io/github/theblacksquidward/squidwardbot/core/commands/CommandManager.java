package io.github.theblacksquidward.squidwardbot.core.commands;

import com.google.common.base.Stopwatch;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private static final Map<String, ISquidwardBotCommand> COMMANDS = new HashMap<>();

    public static void captureAndRegisterCommands(Reflections reflections) {
        LOGGER.info("Beginning to scan for commands...");
        final Stopwatch timer = Stopwatch.createStarted();
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Command.class);
        LOGGER.info("Successfully found " + annotatedClasses.size() + " commands, Attempting to register them...");
        annotatedClasses.forEach((annotatedClass) -> {
            try {
                ISquidwardBotCommand squidwardBotCommand = (ISquidwardBotCommand) annotatedClass.getDeclaredConstructor().newInstance();
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

    private static void registerCommand(ISquidwardBotCommand command) {
        COMMANDS.putIfAbsent(command.getName(), command);
    }

    @NotNull
    @UnmodifiableView
    public static Set<ISquidwardBotCommand> getCommands() {
        return Collections.unmodifiableSet(new HashSet<>(COMMANDS.values()));
    }

    @NotNull
    @UnmodifiableView
    public static Map<String, ISquidwardBotCommand> getCommandsMap() {
        return Collections.unmodifiableMap(COMMANDS);
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
