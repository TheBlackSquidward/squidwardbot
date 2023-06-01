package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.concurrent.BlockingDeque;

public class RemoveCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();

        event.deferReply().queue();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot must be connected to a voice channel to pause the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to pause the queue.")).queue();
            return;
        }
        final int position = event.getOption("position").getAsInt();
        if (AudioManager.isRepeating(guild)) {
            BlockingDeque<AudioTrack> repeatingQueue = AudioManager.getRepeatingQueue(guild);
            if (position > repeatingQueue.size() || position < 1) {
                event.getHook().sendMessageEmbeds(createMusicReply("You cannot remove a track that doesn't exist in the queue.")).queue();
                return;
            }
            AudioManager.removeTrack(guild, position);
            event.getHook().sendMessageEmbeds(createMusicReply("Successfully removed track at position " + position + ".")).queue();
        } else {
            BlockingDeque<AudioTrack> queue = AudioManager.getQueue(guild);
            if (position > queue.size() || position < 1) {
                event.getHook().sendMessageEmbeds(createMusicReply("You cannot remove a track that doesn't exist in the queue.")).queue();
                return;
            }
            AudioManager.removeTrack(guild, position);
            event.getHook().sendMessageEmbeds(createMusicReply("Successfully removed track at position " + position + ".")).queue();

        }
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes the given track from the queue.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(new OptionData(OptionType.INTEGER, "position", "The position of the track to be removed", true));
    }

}
