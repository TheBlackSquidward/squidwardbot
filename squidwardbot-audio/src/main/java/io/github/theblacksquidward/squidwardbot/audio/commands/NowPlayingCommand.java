package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import genius.SongSearch;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Instant;
import java.util.Arrays;

@Command
public class NowPlayingCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.deferReply().addEmbeds(createMusicReply("The bot must be connected to a voice channel to get the currently playing track.")).queue();
            return;
        }
        final AudioTrack currentTrack = AudioManager.getCurrentlyPlayingTrack(guild);
        if(currentTrack == null) {
            event.deferReply().addEmbeds(createMusicReply("There is no song currently playing...")).queue();
            return;
        }
        event.deferReply().addEmbeds(getCurrentTrackEmbed(guild, currentTrack)).queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Returns the currently playing song.";
    }

    private MessageEmbed getCurrentTrackEmbed(Guild guild, AudioTrack currentTrack) {
        AudioTrackInfo currentTrackInfo = currentTrack.getInfo();
        SongSearch.Hit hit = getHit(guild);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setDescription(getProgress(currentTrack));
        if(hit != null) {
            embedBuilder.setThumbnail(hit.getThumbnailUrl());
            embedBuilder.setFooter(hit.getArtist().getName(), hit.getArtist().getImageUrl());
            embedBuilder.setTitle(hit.getTitleWithFeatured(), currentTrackInfo.uri);
        }
        embedBuilder.setThumbnail(currentTrackInfo.artworkUrl);
        embedBuilder.setFooter(currentTrackInfo.author);
        embedBuilder.setTitle(currentTrackInfo.title, currentTrackInfo.uri);
        return embedBuilder.build();
    }

    private String getProgress(AudioTrack nowPlaying) {
        final int percentage = Math.round((float) nowPlaying.getPosition() / nowPlaying.getDuration() * 100);
        return "[**" + StringUtils.millisecondsFormatted(nowPlaying.getPosition()) + "**/**"
                + StringUtils.millisecondsFormatted(nowPlaying.getDuration()) + "**] "
                + createProgressBar(nowPlaying.getDuration(), nowPlaying.getPosition()) + " (" + percentage + "%)";
    }

    private String createProgressBar(long total, long current) {
        final int arraySize = 12;
        final String line = "▬";
        final String slider = "🔘";
        final String[] result = new String[arraySize];
        if (current >= total) {
            Arrays.fill(result, line);
            result[arraySize - 1] = slider;
            return String.join("", result);
        }
        final double percentage = (float) current / total;
        final int progress = (int) Math.max(0, Math.min(Math.round(arraySize * percentage), arraySize - 1));
        for (int index = 0; index < progress; index++) {
            result[index] = line;
        }
        result[progress] = slider;
        for (int index = progress + 1; index < arraySize; index++) {
            result[index] = line;
        }
        return String.join("", result);
    }

}
