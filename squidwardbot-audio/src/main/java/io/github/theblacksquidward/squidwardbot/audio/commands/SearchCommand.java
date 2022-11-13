package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import genius.SongSearch;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Command
public class SearchCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        event.deferReply().queue();

        String identifier = event.getOption("identifier").getAsString();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(event, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
    }

    private static class AudioLoadResultImpl extends BaseAudioLoadResultImpl {

        private final SlashCommandInteractionEvent event;
        private final String identifier;

        public AudioLoadResultImpl(SlashCommandInteractionEvent event, String identifier, TrackScheduler trackScheduler) {
            super(trackScheduler);
            this.event = event;
            this.identifier = identifier;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);

            SongSearch.Hit hit = getHit(event.getGuild(), audioTrack);
            if(hit != null) {
                embedBuilder.setThumbnail(hit.getThumbnailUrl());
                embedBuilder.setFooter(hit.getArtist().getName(), hit.getArtist().getImageUrl());
                embedBuilder.setTitle(hit.getTitleWithFeatured(), audioTrackInfo.uri);
            } else {
                embedBuilder.setThumbnail(audioTrackInfo.artworkUrl);
                embedBuilder.setFooter(audioTrackInfo.author);
                embedBuilder.setTitle(audioTrackInfo.title, audioTrackInfo.uri);
            }
            embedBuilder.addField("Identifier", identifier, false);
            embedBuilder.addField("Duration", StringUtils.millisecondsFormatted(audioTrackInfo.length), false);

            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            event.getHook().sendMessageEmbeds(createMusicReply("Somehow the given identifier: `" + identifier + "` has returned an audio playlist.")).queue();
            super.playlistLoaded(audioPlaylist);
        }

        @Override
        public void noMatches() {
            event.getHook().sendMessageEmbeds(createMusicReply("Could not match the given identifier: `" + identifier + "` to an audio track or an audio playlist.")).queue();
            super.noMatches();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            event.getHook().sendMessageEmbeds(createMusicReply(
                    "Error whilst trying to search for the given identifier. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }

    }

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "Searches the source managers for the given identifier and returns the result.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(new OptionData(OptionType.STRING, "identifier", "Identifier of the track (URL/Name)", true));
    }

}
