package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

@Command
public class NightcoreCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.deferReply().addEmbeds(createMusicReply("The bot must be connected to a voice channel to apply the nightcore effect to the audio player.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in the same voice channel as the bot to apply the nightcore effect to the audio player.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.deferReply().addEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        if(event.getSubcommandName().equalsIgnoreCase("enable")) {
            if(AudioManager.isNightcore(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player already has the nightcore effect applied.")).queue();
                return;
            }
            AudioManager.enableNightcore(guild);
            event.deferReply().addEmbeds(createMusicReply("The nightcore effect has successfully been applied to the audio player.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("disable")) {
            if(!AudioManager.isNightcore(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player does not currently have the nightcore effect applied.")).queue();
                return;
            }
            AudioManager.disableNightcore(guild);
            event.deferReply().addEmbeds(createMusicReply("The nightcore effect has been removed from the audio player.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("get")) {
            if(!AudioManager.isNightcore(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player does not currently have the nightcore effect applied.")).queue();
                return;
            }
            event.deferReply().addEmbeds(createMusicReply("The current nightcore speed is `" + AudioManager.getNightcoreSpeed(guild) + "`.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("set")) {
            if(!AudioManager.isNightcore(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player does not currently have the nightcore effect applied.")).queue();
                return;
            }
            final OptionMapping nightcore = event.getOption("nightcore");
            AudioManager.setNightcoreSpeed(guild, nightcore.getAsDouble());
            event.deferReply().addEmbeds(createMusicReply("The nightcore speed has been set to " + nightcore.getAsDouble() + ".")).queue();
        }
    }

    @Override
    public String getName() {
        return "nightcore";
    }

    @Override
    public String getDescription() {
        return "Adds a nightcore effect to the audio player.";
    }

    @Override
    public List<SubcommandData> getSubcommandData() {
        return List.of(
                new SubcommandData("enable", "Enables the nightcore feature on this audio player."),
                new SubcommandData("disable", "Disables the nightcore feature on this audio player."),
                new SubcommandData("get", "Gets the current nightcore speed."),
                new SubcommandData("set", "Sets the nightcore speed.")
                        .addOptions(new OptionData(OptionType.NUMBER, "nightcore", "The speed of the nightcore effect.", true)
                                .setRequiredRange(0.0, 2.0))
        );
    }

}
