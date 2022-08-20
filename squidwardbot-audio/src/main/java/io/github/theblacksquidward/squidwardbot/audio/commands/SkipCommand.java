package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class SkipCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to skip the currently playing track.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(createMusicReply("You must be in the same voice channel as the bot to skip the currently playing track.")).queue();
            return;
        }
        final AudioTrack removedTrack = AudioManager.skipTrack(guild);
        if(removedTrack == null) {
            event.replyEmbeds(createMusicReply("Could not skip!")).queue();
            return;
        }
        event.replyEmbeds(createMusicReply("Successfully skipped the track: **" + removedTrack.getInfo().title + "**")).queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the audio track that is currently playing to the next audio track in the queue.";
    }

}