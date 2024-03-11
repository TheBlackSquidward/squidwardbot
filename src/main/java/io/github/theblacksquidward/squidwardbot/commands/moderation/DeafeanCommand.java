package io.github.theblacksquidward.squidwardbot.commands.moderation;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Command
public class DeafeanCommand extends SquidwardBotCommand {

  @Override
  public void onSlashCommand(SlashCommandInteractionEvent event) {
    Member member = event.getOption("user").getAsMember();
    event.deferReply().queue();
    if (!event.getMember().hasPermission(Permission.VOICE_DEAF_OTHERS)) {
      event.getHook().sendMessage("You do not have permission to deafen other members.").queue();
    }
    if (member == null) {
      event.getHook().sendMessage("The specified member does not exist...").queue();
    }
    if (!member.getVoiceState().inAudioChannel()) {
      event
          .getHook()
          .sendMessage("You cannot deafen someone who is not currently in a voice channel.")
          .queue();
    }
    member.deafen(true).queue();
    event
        .getHook()
        .sendMessage("You have successfully deafened " + member.getAsMention())
        .setEphemeral(true)
        .queue();
  }

  @Override
  public String getName() {
    return "deafen";
  }

  @Override
  public String getDescription() {
    return "Deafens the specified user.";
  }

  @Override
  public List<OptionData> getOptionData() {
    return List.of(new OptionData(OptionType.USER, "user", "The member to deafen.", true));
  }
}
