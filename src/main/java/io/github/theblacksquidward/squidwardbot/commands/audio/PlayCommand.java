package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.DefaultAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.time.Instant;

@Command
public class PlayCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        //TODO this needs new logic implemented
        Guild guild = event.getGuild();
        if(!guild.getAudioManager().isConnected()) {
            User user = event.getUser();
            Member member = guild.getMember(user);
            if(member == null || member.getVoiceState() == null) {
                event.replyEmbeds(EmbedUtils.createMusicReply("You are not in a channel and the bot is not in a channel.")).queue();
                return;
            }
            GuildVoiceState memberVoiceState = member.getVoiceState();
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        }
        AudioManager.loadAndPlay(guild, event.getOption("identifier").getAsString(), new AudioLoadResultImpl(event, AudioManager.getOrCreate(guild).getTrackScheduler()));
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
    public CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription())
                .addOption(OptionType.STRING, "identifier", "Identifier of the track (URL/Name)", true);
    }

    private static class AudioLoadResultImpl extends DefaultAudioLoadResultImpl {

        private final SlashCommandInteractionEvent event;

        public AudioLoadResultImpl(SlashCommandInteractionEvent event, TrackScheduler trackScheduler) {
            super(trackScheduler);
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            event.replyEmbeds(new EmbedBuilder()
                    .setTimestamp(Instant.now())
                    .setColor(ColorConstants.PRIMARY_COLOR)
                    .setAuthor("|  " + "Successfully loaded " + audioTrackInfo.title + " by " + audioTrackInfo.author + " to the queue.", null, "https://avatars.githubusercontent.com/u/65785034?v=4")
                    .setThumbnail(audioTrackInfo.artworkUrl)
                    .build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            super.playlistLoaded(audioPlaylist);
            event.replyEmbeds(EmbedUtils.createMusicReply("Successfully loaded the playlist: " + audioPlaylist.getName())).queue();
        }

        @Override
        public void noMatches() {
            super.noMatches();
            event.replyEmbeds(EmbedUtils.createMusicReply("Could not match the given identifier to a track.")).queue();
        }

        @Override
        public void loadFailed(FriendlyException e) {
            super.loadFailed(e);
            event.replyEmbeds(EmbedUtils.createMusicReply("Error whilst loading the track.")).queue();
        }

    }

}
