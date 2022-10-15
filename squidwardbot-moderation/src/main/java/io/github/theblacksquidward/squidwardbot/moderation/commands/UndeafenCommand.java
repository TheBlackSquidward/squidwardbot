package io.github.theblacksquidward.squidwardbot.moderation.commands;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@Command
public class UndeafenCommand extends SquidwardBotCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        event.deferReply().queue();
        if(!event.getMember().hasPermission(Permission.VOICE_DEAF_OTHERS)) {
            event.getHook().sendMessage("You do not have permission to undeafen other members.").queue();
        }
        if(member == null) {
            event.getHook().sendMessage("The specified member does not exist...").queue();
        }
        if(!member.getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage("You cannot undeafen someone who is not currently in a voice channel.").queue();
        }
        member.deafen(false).queue();
        event.getHook().sendMessage("You have successfully undeafened " + member.getAsMention()).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "undeafen";
    }

    @Override
    public String getDescription() {
        return "Undeafens the specified user.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(new OptionData(OptionType.USER, "user", "The member to undeafen.", true));
    }

}
