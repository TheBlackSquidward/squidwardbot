package io.github.theblacksquidward.squidwardbot.commands.misc;

import io.github.theblacksquidward.squidwardbot.commands.ICommand;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class MusicCommand implements IGuildCommand {

    static final MessageEmbed MUSIC_HELP_EMBED = new EmbedBuilder()
            .setColor(ColorConstants.MUSIC_COLOR)
            .setTitle("Music Module")
            .setDescription("Here are all the commands from the Music module of SquidwardBot.")
            .addField("Commands",
                    """
                            `help` - Returns the help information for the Music module.\s
                            `disconnect` - Disconnects the bot from the channel it is currently in.\s
                            `skip` - Skips the currently playing track to the next track in the queue.\s
                            `connect <channeL_name>` - Connects the bot to the channel you are currently in or the channel specified.\s
                            `play` - Plays the given song or adds it to the queue if a song is currently playing.\s
                            """, false)
            .setFooter("Coded by TheBlackSquidward", "https://avatars.githubusercontent.com/u/65785034?v=4")
            .build();

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        event.replyEmbeds(MUSIC_HELP_EMBED).queue();
    }

    @Override
    public String getName() {
        return "music";
    }

    //TODO
    @Override
    public String getDescription() {
        return "All things music!";
    }

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription());
    }
}
