package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.github.topisenpai.lavasrc.deezer.DeezerAudioTrack;
import com.github.topisenpai.lavasrc.mirror.MirroringAudioTrack;
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
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

@Command
public class PlayCommand extends AbstractAudioCommand {

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
            guild.getAudioManager().openAudioConnection(audioChannel);
            event.getHook().sendMessageEmbeds(createMusicReply("Successfully connected to " + audioChannel.getName())).queue();
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to add an audio track to the queue.")).queue();
            return;
        }
        final String identifier = event.getOption("identifier").getAsString();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(event, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
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
                new OptionData(OptionType.STRING, "identifier", "Identifier of the track (URL/Name)", true)
        );
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
            trackScheduler.queueTrack(audioTrack);
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setDescription("Successfully queued the track " + audioTrackInfo.title + " by " + audioTrackInfo.author + " [" + StringUtils.millisecondsFormatted(audioTrack.getDuration()) + "] at position #" + AudioManager.getPositionInQueue(event.getGuild(), audioTrack) + " in the queue.");
            SongSearch.Hit hit = getCurrentlyPlayingHit(event.getGuild());
            if (hit != null) {
                embedBuilder.setThumbnail(hit.getThumbnailUrl());
                embedBuilder.setFooter(hit.getArtist().getName(), hit.getArtist().getImageUrl());
                embedBuilder.setTitle(hit.getTitleWithFeatured(), audioTrackInfo.uri);
            } else {
                if(audioTrack instanceof MirroringAudioTrack mirroringAudioTrack) embedBuilder.setThumbnail(mirroringAudioTrack.getArtworkURL());
                if(audioTrack instanceof DeezerAudioTrack deezerAudioTrack) embedBuilder.setThumbnail(deezerAudioTrack.getArtworkURL());
                embedBuilder.setFooter(audioTrackInfo.author);
                embedBuilder.setTitle(audioTrackInfo.title, audioTrackInfo.uri);
            }
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            audioPlaylist.getTracks().forEach(trackScheduler::queueTrack);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            List<String> formattedPlaylistTracks = audioPlaylist.getTracks().stream()
                    .map(audioTrack -> audioTrack.getInfo().title + " by " + audioTrack.getInfo().author)
                    .toList();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setTitle(audioPlaylist.getName());
            embedBuilder.setDescription(StringUtils.getIndentedStringList(formattedPlaylistTracks));
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
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
                    "Error whilst queueing the track/playlist. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }

    }

}
