package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

@Command
public class QueueCommand implements IGuildCommand {

    private static final int MAX_TRACKS_ON_PAGE = 20;

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!AudioUtils.hasPlayer(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("This guild does not have a player...")).queue();
            return;
        }
        if(AudioUtils.isQueueEmpty(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("The queue is currently empty.")).queue();
            return;
        }
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
        int trackCount = Math.min(AudioUtils.getQueueSize(guild), MAX_TRACKS_ON_PAGE);
        List<AudioTrack> tracks = AudioUtils.getQueuedTracks(guild);

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
                    .append(AudioUtils.formatTrackTimeDuration(track.getDuration()))
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
