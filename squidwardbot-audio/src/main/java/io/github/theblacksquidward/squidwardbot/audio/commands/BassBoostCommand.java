package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class BassBoostCommand extends AbstractAudioCommand{

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.deferReply().addEmbeds(createMusicReply("The bot must be connected to a voice channel to bass boost a song.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in the same voice channel as the bot to bass boost a song.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.deferReply().addEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        if(event.getSubcommandName().equalsIgnoreCase("enable")) {
            if(AudioManager.isBassBoosted(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player is already bass boosted.")).queue();
                return;
            }
            AudioManager.enableBassBoost(guild);
            event.deferReply().addEmbeds(createMusicReply("Bass boost has been enabled for this audio player.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("disable")) {
            if(!AudioManager.isBassBoosted(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player is not currently bass boosted.")).queue();
                return;
            }
            AudioManager.disableBassBoost(guild);
            event.deferReply().addEmbeds(createMusicReply("Bass boost has been disabled for this audio player.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("get")) {
            if(!AudioManager.isBassBoosted(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player is not currently bass boosted.")).queue();
                return;
            }
            event.deferReply().addEmbeds(createMusicReply("The current bass boost percentage is " + AudioManager.getBassBoostPercentage(guild) + "%.")).queue();
        }
        if(event.getSubcommandName().equalsIgnoreCase("set")) {
            if(!AudioManager.isBassBoosted(guild)) {
                event.deferReply().addEmbeds(createMusicReply("The audio player is not currently bass boosted.")).queue();
                return;
            }
            final OptionMapping bassBoostMultiplier = event.getOption("bassboost");
            AudioManager.setBassBoostMultiplier(guild, bassBoostMultiplier.getAsInt());
            event.deferReply().addEmbeds(createMusicReply("The bass boost multiplier has been set to " + bassBoostMultiplier.getAsInt() + "%.")).queue();
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
                new SubcommandData("get", "Gets the current bass boost level."),
                new SubcommandData("set", "Sets the bass boost level.")
                        .addOptions(new OptionData(OptionType.INTEGER, "bassboost", "The percentage bass boost level.", true)
                                .setRequiredRange(0, 100))
        );
    }

}
