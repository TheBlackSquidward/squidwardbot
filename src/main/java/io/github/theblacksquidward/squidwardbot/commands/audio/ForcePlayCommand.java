package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Command
public class ForcePlayCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        //TODO this needs redoing with the new logic
        Guild guild = event.getGuild();
        if(!guild.getAudioManager().isConnected()) {
            User user = event.getUser();
            Member member = guild.getMember(user);
            if(member == null || member.getVoiceState() == null) {
                event.replyEmbeds(EmbedUtils.createMusicReply("You are not in a channel and the bot is not in a channel.")).queue();
                return;
            }
            GuildVoiceState memberVoiceState = member.getVoiceState();
            guild.getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        }
        AudioManager.loadAndPlay(guild, event.getOption("identifier").getAsString(), new AudioLoadResultImpl(event, AudioManager.getOrCreate(guild).getTrackScheduler()));
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

        public AudioLoadResultImpl(SlashCommandInteractionEvent event, TrackScheduler trackScheduler) {
            super(trackScheduler);
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            super.trackLoaded(audioTrack);
            trackScheduler.forcePlayTrack(audioTrack);
            event.reply("forced track").queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            super.playlistLoaded(audioPlaylist);
            event.reply("Tried to force playlist").queue();
        }

        @Override
        public void noMatches() {
            super.noMatches();
            event.reply("No Match").queue();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            super.loadFailed(exception);
            event.reply("Load failed").queue();
        }
    }

}
