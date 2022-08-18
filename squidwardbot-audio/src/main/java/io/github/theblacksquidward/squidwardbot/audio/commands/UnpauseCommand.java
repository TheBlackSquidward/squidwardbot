package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class UnpauseCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to unpause the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(createMusicReply("You must be in the same voice channel as the bot to unpause the queue.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.replyEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        if(!AudioManager.isPaused(guild)) {
           event.replyEmbeds(createMusicReply("The player is not currently paused...")).queue();
           return;
        }
        AudioManager.resumeTrack(guild);
        event.replyEmbeds(createMusicReply("Successfully unpaused the player.")).queue();
    }

    @Override
    public String getName() {
        return "unpause";
    }

    @Override
    public String getDescription() {
        return "Unpauses the currently playing song.";
    }

}
