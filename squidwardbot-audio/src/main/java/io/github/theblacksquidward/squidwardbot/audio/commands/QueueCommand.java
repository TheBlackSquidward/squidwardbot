package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

@Command
public class QueueCommand extends AbstractAudioCommand {

    private static final int MAX_TRACKS_ON_PAGE = 20;

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to view the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(createMusicReply("You must be in the same voice channel as the bot to view the queue.")).queue();
            return;
        }
        if(AudioManager.isQueueEmpty(guild)) {
            event.replyEmbeds(createMusicReply("Could not view the queue as it is currently empty.")).queue();
            return;
        }
        //TODO redo with pagnation
        event.replyEmbeds(getQueueEmbed(guild)).queue();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Returns the current queue.";
    }

    //TODO this needs to be changed in favor of pagnation
    private MessageEmbed getQueueEmbed(Guild guild) {
        return new EmbedBuilder()
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setAuthor("|  " + "Current Queue:", null, "https://avatars.githubusercontent.com/u/65785034?v=4")
                .setDescription(getQueueAsString(guild))
                .build();
    }

    private String getQueueAsString(Guild guild) {
        StringBuilder stringBuilder = new StringBuilder();
        int trackCount = Math.min(AudioManager.getQueueSize(guild), MAX_TRACKS_ON_PAGE);
        List<AudioTrack> tracks = AudioManager.getQueuedTracks(guild);

        for(int i = 0; i < trackCount; i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo trackInfo = track.getInfo();
            stringBuilder.append('#')
                    .append(i + 1)
                    .append("  `")
                    .append(trackInfo.title)
                    .append(" by ")
                    .append(trackInfo.author)
                    .append("`  [`")
                    //TODO i dont like this
                    .append(AudioManager.formatTrackTimeDuration(track.getDuration()))
                    .append("`]\n");
        }
        if(tracks.size() > trackCount) {
            stringBuilder.append("And `")
                    .append(tracks.size() - trackCount)
                    .append("` more...");
        }
        return stringBuilder.toString();
    }

}
