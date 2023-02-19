package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class NightcoreCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        event.deferReply().queue();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot must be connected to a voice channel to apply the nightcore effect to the audio player.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to apply the nightcore effect to the audio player.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        if(AudioManager.isSlowed(guild)) {
            event.getHook().sendMessageEmbeds(createMusicReply("The nightcore effect cannot be applied to the audio player as it currently has the slow effect.")).queue();
            return;
        }
        if(AudioManager.isNightcore(guild)) {
            event.getHook().sendMessageEmbeds(createMusicReply("The nightcore effect has been removed from the player.")).queue();
            AudioManager.disableNightcore(guild);
            return;
        }
        event.getHook().sendMessageEmbeds(createMusicReply("The nightcore effect has been applied to the player.")).queue();
        AudioManager.enableNightcore(guild);
    }

    @Override
    public String getName() {
        return "nightcore";
    }

    @Override
    public String getDescription() {
        return "Adds a nightcore effect to the audio player.";
    }

}
