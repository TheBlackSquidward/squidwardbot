package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ShuffleCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("The bot must be connected to a voice channel to shuffle the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("You must be in the same voice channel as the bot to shuffle the queue.")).queue();
            return;
        }
        if(AudioManager.isQueueEmpty(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("Could not shuffle the queue as it is empty.")).queue();
            return;
        }
        AudioManager.shuffleQueue(guild);
        event.replyEmbeds(EmbedUtils.createMusicReply("Successfully shuffled the queue.")).queue();
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
