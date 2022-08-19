package io.github.theblacksquidward.squidwardbot.core.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class SquidwardBotCommand extends ListenerAdapter implements ISquidwardBotCommand{

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.isFromGuild()) {
            if(event.getName().equalsIgnoreCase(getName())) {
                onSlashCommand(event);
            }
        }
    }

}
