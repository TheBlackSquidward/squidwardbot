package io.github.theblacksquidward.squidwardbot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandHandler extends ListenerAdapter {

    private static final Map<String, IGuildCommand> GUILD_COMMANDS = new HashMap<>();
    private static final Map<String, IGlobalCommand> GLOBAL_COMMANDS = new HashMap<>();

    public static void captureAndRegisterCommands(Reflections reflections) {
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Command.class);
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
                e.printStackTrace();
            }
        });
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
