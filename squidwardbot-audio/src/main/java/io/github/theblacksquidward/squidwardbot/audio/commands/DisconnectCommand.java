package io.github.theblacksquidward.squidwardbot.audio.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DisconnectCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        event.deferReply().queue();
        if(!guild.getAudioManager().isConnected()) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot is currently not in a channel.")).queue();
        }
        final AudioChannel audioChannel = guild.getAudioManager().getConnectedChannel();
        guild.getAudioManager().closeAudioConnection();
        event.getHook().sendMessageEmbeds(createMusicReply("Successfully disconnected from " + audioChannel.getName() + ".")).queue();
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
