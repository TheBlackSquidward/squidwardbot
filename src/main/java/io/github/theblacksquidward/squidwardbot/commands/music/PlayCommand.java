package io.github.theblacksquidward.squidwardbot.commands.music;

import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class PlayCommand implements IGuildCommand {

    //TODO all messages should be embeds
    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        if(!guild.getAudioManager().isConnected()) {
            Member member = guild.getMember(user);
            GuildVoiceState memberVoiceState = member.getVoiceState();
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, memberVoiceState.getChannel());
            event.reply("Successfully connected to " + memberVoiceState.getChannel().getName()).queue();
        }
        AudioUtils.loadAndPlay(guild, event.getOption("identifier").getAsString());
        //TODO msg is ass
        event.reply("Successfully played a track.").queue();
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays the given song.";
    }

    //TODO
    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription())
                //TODO check if this should be unknown
                .addOption(OptionType.STRING, "identifier", "Identifier of the track (URL)", true);
    }

}
