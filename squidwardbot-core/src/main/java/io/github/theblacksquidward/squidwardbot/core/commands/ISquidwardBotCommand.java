package io.github.theblacksquidward.squidwardbot.core.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ISquidwardBotCommand {

    void onSlashCommand(SlashCommandInteractionEvent event);

    String getName();
    String getDescription();

    default boolean isGuildOnly() {
        return true;
    }

    default List<OptionData> getOptionData() {
        return Collections.emptyList();
    }

    default List<SubcommandData> getSubcommandData() {
        return Collections.emptyList();
    }

    default CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription())
                .addSubcommands(getSubcommandData())
                .addOptions(getOptionData())
                .setGuildOnly(true);
    }

}
