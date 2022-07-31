package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class DisconnectCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!guild.getAudioManager().isConnected()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("The bot is currently not in a channel.")).queue();
        }
        final AudioChannel audioChannel = guild.getAudioManager().getConnectedChannel();
        guild.getAudioManager().closeAudioConnection();
        event.replyEmbeds(EmbedUtils.createMusicReply("Successfully disconnected from " + audioChannel.getName() + ".")).queue();
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
