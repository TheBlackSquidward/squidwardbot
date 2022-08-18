package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class RepeatCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to repeat a track.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(createMusicReply("You must be in the same voice channel as the bot to repeat a track.")).queue();
            return;
        }
        if(AudioManager.getCurrentlyPlayingTrack(guild) == null) {
            event.replyEmbeds(createMusicReply("Could not repeat as there is no currently playing track.")).queue();
            return;
        }
        AudioManager.repeatTrack(guild);
        event.replyEmbeds(createMusicReply("Successfully repeated the currently playing song.")).queue();
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getDescription() {
        return "Repeats the currently playing track.";
    }

}
