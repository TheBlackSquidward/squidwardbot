package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.time.Instant;
import java.util.List;

@Command
public class ForcePlayCommand extends AbstractAudioCommand {

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
            event.deferReply().addEmbeds(createMusicReply("You must be in the same voice channel as the bot to force play an audio track.")).queue();
            return;
        }
        final String identifier = event.getOption("identifier").getAsString();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(reply, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
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

        private final ReplyCallbackAction replyCallbackAction;
        private final String identifier;

        public AudioLoadResultImpl(ReplyCallbackAction replyCallbackAction, String identifier, TrackScheduler trackScheduler) {
            super(trackScheduler);
            this.replyCallbackAction = replyCallbackAction;
            this.identifier = identifier;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            trackScheduler.forceQueueTrack(audioTrack);
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            replyCallbackAction.addEmbeds(new EmbedBuilder()
                    .setTimestamp(Instant.now())
                    .setColor(ColorConstants.PRIMARY_COLOR)
                    .setAuthor("|  " + "Successfully force loaded " + audioTrackInfo.title + " by " + audioTrackInfo.author + " to the queue.", null, "https://avatars.githubusercontent.com/u/65785034?v=4")
                    .setThumbnail(audioTrackInfo.artworkUrl)
                    .build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            trackScheduler.forceQueueTracks(audioPlaylist.getTracks());
            //TODO create cool embed
            replyCallbackAction.addEmbeds(createMusicReply("Successfully force loaded the playlist: " + audioPlaylist.getName())).queue();
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
                    "Error whilst trying to force queue the track/playlist. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }
    }

}
