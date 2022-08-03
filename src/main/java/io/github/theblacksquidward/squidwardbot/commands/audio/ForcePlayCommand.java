package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.time.Instant;

@Command
public class ForcePlayCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            guild.getAudioManager().openAudioConnection(audioChannel);
            event.getChannel().sendMessageEmbeds(EmbedUtils.createMusicReply("Successfully connected to " + audioChannel.getName())).queue();
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("You must be in the same voice channel as the bot to force play an audio track.")).queue();
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
    public CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription())
                .addOption(OptionType.STRING, "identifier", "Identifier of the track (URL/Name)", true);
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
            event.replyEmbeds(new EmbedBuilder()
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
            event.replyEmbeds(EmbedUtils.createMusicReply("Successfully force loaded the playlist: " + audioPlaylist.getName())).queue();
            super.playlistLoaded(audioPlaylist);
        }

        @Override
        public void noMatches() {
            event.replyEmbeds(EmbedUtils.createMusicReply("Could not match the given identifier: `" + identifier + "` to an audio track or an audio playlist.")).queue();
            super.noMatches();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            event.replyEmbeds(EmbedUtils.createMusicReply(
                    "Error whilst loading the track. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }
    }

}
