package io.github.theblacksquidward.squidwardbot.audio.commands;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ShuffleCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.deferReply().addEmbeds(createMusicReply("The bot must be connected to a voice channel to shuffle the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in the same voice channel as the bot to shuffle the queue.")).queue();
            return;
        }
        if(AudioManager.isQueueEmpty(guild)) {
            event.deferReply().addEmbeds(createMusicReply("Could not shuffle the queue as it is empty.")).queue();
            return;
        }
        AudioManager.shuffleQueue(guild);
        event.deferReply().addEmbeds(createMusicReply("Successfully shuffled the queue.")).queue();
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "Shuffles the current queue.";
    }

}
