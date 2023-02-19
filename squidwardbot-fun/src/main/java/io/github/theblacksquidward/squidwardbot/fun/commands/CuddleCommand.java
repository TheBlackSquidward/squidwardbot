package io.github.theblacksquidward.squidwardbot.fun.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.List;

@Command
public class CuddleCommand extends AbstractGifCommand {

    //TODO rename
    private final String[] MESSAGES = {
            "%giver% cuddles %receiver%!",
            "%giver% gives %receiver% a cuddle!"
    };

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Member giver = event.getMember();
        Member receiver = event.getOption("user").getAsMember();
        if(giver == receiver) {
            MessageEmbed embed = getLonelyEmbedBuilder()
                    .setColor(Color.BLACK)
                    .setDescription("Awwwwww is someone lonely " + giver.getAsMention() + ". You cannot cuddle yourself.")
                    .build();
            event.replyEmbeds(embed).queue();
            return;
        }
        MessageEmbed embed = getGifEmbedBuilder("anime cuddle")
                .setColor(ColorConstants.TERTIARY_COLOR)
                .setDescription(getRandomMessage(MESSAGES)
                        .replace("%giver%", giver.getAsMention())
                        .replace("%receiver%", receiver.getAsMention()))
                .build();
        event.replyEmbeds(embed).queue();
    }

    @Override
    public String getName() {
        return "cuddle";
    }

    @Override
    public String getDescription() {
        return "Cuddles the specified user.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(
            new OptionData(OptionType.USER, "user", "Your cuddle buddy!", true)
        );
    }

}
