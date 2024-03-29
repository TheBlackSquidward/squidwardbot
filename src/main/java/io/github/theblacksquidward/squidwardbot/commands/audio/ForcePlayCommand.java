package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylistInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

@Command
public class ForcePlayCommand extends AbstractAudioCommand {

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
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to force play an audio track.")).queue();
            return;
        }
        final String identifier = event.getOption("identifier").getAsString();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(event, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
    }

    @Override
    public String getName() {
        return "forceplay";
    }

    @Override
    public String getDescription() {
        return "Forces the given song to the top of the queue and plays it.";
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
            trackScheduler.forceQueueTrack(audioTrack);
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setDescription("Successfully force loaded the track " + audioTrackInfo.title + " by " + audioTrackInfo.author + " [" + StringUtils.millisecondsFormatted(audioTrack.getDuration()) + "] to the queue.");
            embedBuilder.setThumbnail(audioTrackInfo.artworkUrl);
            embedBuilder.setFooter(audioTrackInfo.author);
            embedBuilder.setTitle(audioTrackInfo.title, audioTrackInfo.uri);
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            audioPlaylist.getTracks().forEach(trackScheduler::queueTrack);
            AudioPlaylistInfo audioPlaylistInfo = audioPlaylist.getAudioPlaylistInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            List<String> formattedPlaylistTracks = audioPlaylist.getTracks().stream()
                    .map(audioTrack -> audioTrack.getInfo().title + " by " + audioTrack.getInfo().author)
                    .toList();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setDescription(StringUtils.getIndentedStringList(formattedPlaylistTracks));
            // embedBuilder.setThumbnail(audioPlaylistInfo.artworkUrl());
            // embedBuilder.setFooter(audioPlaylistInfo.owner(), audioPlaylistInfo.ownerThumbnailUrl());
            embedBuilder.setFooter(audioPlaylistInfo.getOwner());
            embedBuilder.setTitle(audioPlaylistInfo.getName(), audioPlaylistInfo.getUri());
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
                    "Error whilst trying to force queue the track/playlist. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }
    }

}
