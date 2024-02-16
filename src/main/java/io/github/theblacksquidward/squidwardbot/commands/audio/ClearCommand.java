package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ClearCommand extends AbstractAudioCommand {

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
              createMusicReply("The bot must be connected to a voice channel to clear the queue."))
          .queue();
      return;
    }
    if (event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "You must be in the same voice channel as the bot to clear the queue."))
          .queue();
      return;
    }
    if (AudioManager.isRepeating(guild)) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "You cannot clear the repeating queue. You must toggle repeating off and then clear."));
    } else {
      if (AudioManager.getQueue(guild).isEmpty()) {
        event
            .getHook()
            .sendMessageEmbeds(
                createMusicReply("The queue is currently empty... There is nothing to skip."))
            .queue();
        return;
      }
      AudioManager.clearQueue(guild);
      event
          .getHook()
          .sendMessageEmbeds(createMusicReply("Successfully cleared the queue."))
          .queue();
    }
  }

  @Override
  public String getName() {
    return "clear";
  }

  @Override
  public String getDescription() {
    return "Clears the current audio track queue.";
  }
}
