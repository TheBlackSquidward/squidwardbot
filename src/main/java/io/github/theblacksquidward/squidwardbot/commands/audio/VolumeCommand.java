package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

@Command
public class VolumeCommand extends AbstractAudioCommand {

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
              createMusicReply("The bot must be connected to a voice channel to pause the queue."))
          .queue();
      return;
    }
    if (event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "You must be in the same voice channel as the bot to pause the queue."))
          .queue();
      return;
    }
    if (event.getSubcommandName().equalsIgnoreCase("get")) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "The current volume of the bot is `" + AudioManager.getVolume(guild) + "`."))
          .queue();
      return;
    }
    if (event.getSubcommandName().equalsIgnoreCase("reset")) {
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "The volume of the audio player has been reset to the default value of `100`."))
          .queue();
      AudioManager.setVolume(guild, 100);
      return;
    }
    if (event.getSubcommandName().equalsIgnoreCase("set")) {
      final OptionMapping volume = event.getOption("volume");
      AudioManager.setVolume(guild, volume.getAsInt());
      event
          .getHook()
          .sendMessageEmbeds(
              createMusicReply(
                  "The volume has successfully been set to `"
                      + AudioManager.getVolume(guild)
                      + "`."))
          .queue();
    }
  }

  @Override
  public String getName() {
    return "volume";
  }

  @Override
  public String getDescription() {
    return "Controls the volume of the audio player.";
  }

  @Override
  public List<SubcommandData> getSubcommandData() {
    return List.of(
        new SubcommandData("get", "Gets the volume of the audio player."),
        new SubcommandData("reset", "Resets the volume back to the default value of 100."),
        new SubcommandData(
                "set", "Sets the volume to the given integer. Must be a value between 0 and 1000.")
            .addOptions(
                new OptionData(
                        OptionType.INTEGER, "volume", "The volume of the audio player.", true)
                    .setRequiredRange(0, 1000)));
  }
}
