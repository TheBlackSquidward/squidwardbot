package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class UnpauseCommand extends AbstractAudioCommand {

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
                  "The bot must be connected to a voice channel to unpause the queue."))
          .queue();
      return;
    }
    if (event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "You must be in the same voice channel as the bot to unpause the queue."))
          .queue();
      return;
    }
    if (!AudioManager.isPlayingTrack(guild)) {
      event
          .getHook()
          .sendMessageEmbeds(createMusicReply("The bot is not currently playing anything..."))
          .queue();
      return;
    }
    if (!AudioManager.isPaused(guild)) {
      event
          .getHook()
          .sendMessageEmbeds(createMusicReply("The player is not currently paused..."))
          .queue();
      return;
    }
    AudioManager.resumeTrack(guild);
    event
        .getHook()
        .sendMessageEmbeds(createMusicReply("Successfully unpaused the player."))
        .queue();
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
