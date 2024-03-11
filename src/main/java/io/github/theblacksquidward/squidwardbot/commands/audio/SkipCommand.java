package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class SkipCommand extends AbstractAudioCommand {

  @Override
  public void onSlashCommand(SlashCommandInteractionEvent event) {
    Guild guild = event.getGuild();
    event.deferReply().queue();
    if (!event.getMember().getVoiceState().inAudioChannel()) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply("You must be in a voice channel to use this command."))
          .queue();
      return;
    }
    final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
    if (!event.getGuild().getAudioManager().isConnected()) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "The bot must be connected to a voice channel to skip the currently playing track."))
          .queue();
      return;
    }
    if (event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "You must be in the same voice channel as the bot to skip the currently playing track."))
          .queue();
      return;
    }
    final AudioTrack removedTrack = AudioManager.skipTrack(guild);
    if (removedTrack == null) {
      event.getHook().sendMessageEmbeds(createMusicReply("Could not skip!")).queue();
      return;
    }
    event
        .getHook()
        .sendMessageEmbeds(
            createMusicReply(
                "Successfully skipped the track: **" + removedTrack.getInfo().title + "**"))
        .queue();
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
