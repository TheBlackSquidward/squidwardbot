package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class RepeatCommand extends AbstractAudioCommand {

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
            event.getHook().sendMessageEmbeds(createMusicReply("The bot must be connected to a voice channel to repeat a track.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to repeat a track.")).queue();
            return;
        }
        if(AudioManager.getCurrentlyPlayingTrack(guild) == null) {
            event.getHook().sendMessageEmbeds(createMusicReply("Could not repeat as there is no currently playing track.")).queue();
            return;
        }
        AudioManager.toggleRepeating(guild);
        event.getHook().sendMessageEmbeds(createMusicReply("Repeating has been toggled to `" + AudioManager.isRepeating(guild) + "`.")).queue();
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getDescription() {
        return "Repeats the currently playing track.";
    }

}
