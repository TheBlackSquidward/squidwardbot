package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.List;

@Command
public class SearchCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        String identifier = event.getOption("identifier").getAsString();
        ReplyCallbackAction replyCallbackAction = event.deferReply();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(replyCallbackAction, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
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
        public void trackLoaded(AudioTrack track) {
            super.trackLoaded(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            super.playlistLoaded(playlist);
        }

        @Override
        public void noMatches() {
            replyCallbackAction.addEmbeds(createMusicReply("Could not match the given identifier: `" + identifier + "` to an audio track or an audio playlist.")).queue();
            super.noMatches();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            replyCallbackAction.addEmbeds(createMusicReply(
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
