package io.github.theblacksquidward.squidwardbot.fun.commands;

import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import java.awt.*;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Command
public class KissCommand extends AbstractGifCommand {

  @Override
  public void onSlashCommand(SlashCommandInteractionEvent event) {
    Member giver = event.getMember();
    Member receiver = event.getOption("user").getAsMember();
    if (giver == receiver) {
      MessageEmbed embed =
          getLonelyEmbedBuilder()
              .setColor(Color.BLACK)
              .setDescription(
                  "Awwwwww is someone lonely "
                      + giver.getAsMention()
                      + ". You cannot kiss yourself.")
              .build();
      event.replyEmbeds(embed).queue();
      return;
    }
    MessageEmbed embed =
        getGifEmbedBuilder("anime kiss")
            .setColor(ColorConstants.TERTIARY_COLOR)
            .setDescription(giver.getAsMention() + " kisses " + receiver.getAsMention() + "!")
            .build();
    event.replyEmbeds(embed).queue();
  }

  @Override
  public String getName() {
    return "kiss";
  }

  @Override
  public String getDescription() {
    return "Kisses the specified user.";
  }

  @Override
  public java.util.List<OptionData> getOptionData() {
    return List.of(new OptionData(OptionType.USER, "user", "Your kissing buddy!", true));
  }
}
