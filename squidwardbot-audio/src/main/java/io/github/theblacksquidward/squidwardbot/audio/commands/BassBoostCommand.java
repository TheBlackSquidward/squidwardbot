package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

@Command
public class BassBoostCommand extends AbstractAudioCommand{

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to bass boost a song.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(createMusicReply("You must be in the same voice channel as the bot to bass boost a song.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.replyEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        if(event.getSubcommandName().equalsIgnoreCase("enable")) {
            if(AudioManager.isBassBoosted(guild)) {
                event.replyEmbeds(createMusicReply("The audio player is already bass boosted.")).queue();
                return;
            }
            AudioManager.enableBassBoost(guild);
            event.replyEmbeds(createMusicReply("Bass boost has been enabled for this audio player.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("disable")) {
            if(!AudioManager.isBassBoosted(guild)) {
                event.replyEmbeds(createMusicReply("The audio player is not currently bass boosted.")).queue();
                return;
            }
            AudioManager.disableBassBoost(guild);
            event.replyEmbeds(createMusicReply("Bass boost has been disabled for this audio player.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("set")) {
            if(!AudioManager.isBassBoosted(guild)) {
                event.replyEmbeds(createMusicReply("The audio player is not currently bass boosted.")).queue();
                return;
            }
            final OptionMapping bassBoostLevel = event.getOption("bassboost");
            AudioManager.setBassBoostLevel(guild, bassBoostLevel.getAsInt());
            event.replyEmbeds(createMusicReply("The bass boost level has been set to " + bassBoostLevel.getAsInt() + "%.")).queue();
        }
    }

    @Override
    public String getName() {
        return "bassboost";
    }

    @Override
    public String getDescription() {
        return "Bass boosts the audio player";
    }

    @Override
    public List<SubcommandData> getSubcommandData() {
        return List.of(
                new SubcommandData("enable", "Enables the bass boost feature on this audio player."),
                new SubcommandData("disable", "Disables the bass boost feature on this audio player."),
                new SubcommandData("set", "Sets the bass boost level.")
                        .addOptions(new OptionData(OptionType.INTEGER, "bassboost", "The percentage bass boost level.", true)
                                .setRequiredRange(0, 100))
        );
    }

}
