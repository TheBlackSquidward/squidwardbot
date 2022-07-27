package io.github.theblacksquidward.squidwardbot.commands;

import com.google.common.base.Stopwatch;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private static final Map<String, IGuildCommand> GUILD_COMMANDS = new HashMap<>();
    private static final Map<String, IGlobalCommand> GLOBAL_COMMANDS = new HashMap<>();

    public static void captureAndRegisterCommands(Reflections reflections) {
        LOGGER.info("Beginning to scan for commands...");
        final Stopwatch timer = Stopwatch.createStarted();
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Command.class);
        LOGGER.info("Successfully found " + annotatedClasses.size() + " commands, Attempting to register them...");
        annotatedClasses.forEach((annotatedClass) -> {
            try {
                ICommand command = (ICommand) annotatedClass.getDeclaredConstructor().newInstance();
                if(command instanceof IGuildCommand iGuildCommand) {
                    registerGuildCommand(iGuildCommand);
                }
                if(command instanceof IGlobalCommand iGlobalCommand) {
                    registerGlobalCommand(iGlobalCommand);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                //TODO log better
                e.printStackTrace();
            }
        });
        LOGGER.debug("Loaded global commands: {}", getGlobalCommands().stream()
                .map(ICommand::getName)
                .collect(Collectors.joining("\n\t", "\n\t", "")));
        LOGGER.debug("Loaded guild commands: {}", getGuildCommands().stream()
                .map(ICommand::getName)
                .collect(Collectors.joining("\n\t", "\n\t", "")));
        timer.stop();
        LOGGER.info("Finished capturing and registering commands in {}. Successfully registered " + GUILD_COMMANDS.size() + " guild commands and " + GLOBAL_COMMANDS.size() + " global commands.", timer);
    }

    private static void registerGuildCommand(IGuildCommand guildCommand) {
        GUILD_COMMANDS.putIfAbsent(guildCommand.getName(), guildCommand);
    }

    private static void registerGlobalCommand(IGlobalCommand globalCommand) {
        GLOBAL_COMMANDS.putIfAbsent(globalCommand.getName(), globalCommand);
    }

    @NotNull
    @UnmodifiableView
    public static Set<IGuildCommand> getGuildCommands() {
        return Collections.unmodifiableSet(new HashSet<>(GUILD_COMMANDS.values()));
    }
    @NotNull
    @UnmodifiableView
    public static Set<IGlobalCommand> getGlobalCommands() {
        return Collections.unmodifiableSet(new HashSet<>(GLOBAL_COMMANDS.values()));
    }

    @NotNull
    @UnmodifiableView
    public static Map<String, ICommand> getAllCommands() {
        HashMap<String, ICommand> allCommands = new HashMap<>();
        allCommands.putAll(GLOBAL_COMMANDS);
        allCommands.putAll(GUILD_COMMANDS);
        return Collections.unmodifiableMap(allCommands);
    }

    //TODO redo
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        ICommand command = getAllCommands().get(event.getName());
        if(command == null) {
            return;
        }
        if(event.getGuild() != null) {
            command.onSlashCommand(event);
            return;
        }
        command.onSlashCommand(event);
    }

}
