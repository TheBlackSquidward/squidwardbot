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
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.time.Instant;
import java.util.List;

@Command
public class PlayCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        final ReplyCallbackAction reply = event.deferReply();
        if(!event.getGuild().getAudioManager().isConnected()) {
            guild.getAudioManager().openAudioConnection(audioChannel);
            reply.addEmbeds(createMusicReply("Successfully connected to " + audioChannel.getName()));
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.deferReply().addEmbeds(createMusicReply("You must be in the same voice channel as the bot to add an audio track to the queue.")).queue();
            return;
        }
        final String identifier = event.getOption("identifier").getAsString();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(event, reply, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays the given song.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(
                new OptionData(OptionType.STRING, "identifier", "Identifier of the track (URL/Name)", false)
        );
    }

    private static class AudioLoadResultImpl extends BaseAudioLoadResultImpl {

        private final ReplyCallbackAction replyCallbackAction;
        private final SlashCommandInteractionEvent event;
        private final String identifier;

        public AudioLoadResultImpl(SlashCommandInteractionEvent event, ReplyCallbackAction replyCallbackAction, String identifier, TrackScheduler trackScheduler) {
            super(trackScheduler);
            this.replyCallbackAction = replyCallbackAction;
            this.event = event;
            this.identifier = identifier;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            trackScheduler.queueTrack(audioTrack);
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setDescription("Successfully queued the track " + audioTrackInfo.title + " by " + audioTrackInfo.author + " [" + StringUtils.millisecondsFormatted(audioTrack.getDuration()) + "] at position #" + AudioManager.getPositionInQueue(event.getGuild(), audioTrack) + " in the queue.");
            SongSearch.Hit hit = AbstractAudioCommand.getHit(event.getGuild());
            if (hit != null) {
                embedBuilder.setThumbnail(hit.getThumbnailUrl());
                embedBuilder.setFooter(hit.getArtist().getName(), hit.getArtist().getImageUrl());
                embedBuilder.setTitle(hit.getTitleWithFeatured(), audioTrackInfo.uri);
            } else {
                embedBuilder.setThumbnail(audioTrackInfo.artworkUrl);
                embedBuilder.setFooter(audioTrackInfo.author);
                embedBuilder.setTitle(audioTrackInfo.title, audioTrackInfo.uri);
            }
            replyCallbackAction.addEmbeds(embedBuilder.build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            audioPlaylist.getTracks().forEach(trackScheduler::queueTrack);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            List<String> formattedPlaylistTracks = audioPlaylist.getTracks().stream().map(audioTrack -> {
                AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
                return audioTrackInfo.title + " by " + audioTrackInfo.author;
            }).toList();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setTitle(audioPlaylist.getName());
            embedBuilder.setDescription(StringUtils.getIndentedStringList(formattedPlaylistTracks));
            replyCallbackAction.addEmbeds(embedBuilder.build()).queue();
            super.playlistLoaded(audioPlaylist);
        }

        @Override
        public void noMatches() {
            replyCallbackAction.addEmbeds(createMusicReply("Could not match the given identifier: `" + identifier + "` to an audio track or an audio playlist.")).queue();
            super.noMatches();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            replyCallbackAction.addEmbeds(createMusicReply(
                    "Error whilst queueing the track/playlist. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }

    }

}
