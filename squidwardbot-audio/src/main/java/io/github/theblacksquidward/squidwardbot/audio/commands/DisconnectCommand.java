package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class DisconnectCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!guild.getAudioManager().isConnected()) {
            event.deferReply().addEmbeds(createMusicReply("The bot is currently not in a channel.")).queue();
        }
        final AudioChannel audioChannel = guild.getAudioManager().getConnectedChannel();
        guild.getAudioManager().closeAudioConnection();
        event.deferReply().addEmbeds(createMusicReply("Successfully disconnected from " + audioChannel.getName() + ".")).queue();
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public String getDescription() {
        return "Disconnect the bot from the channel its currently in.";
    }

}
