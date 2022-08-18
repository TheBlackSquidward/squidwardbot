package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

//TODO should have a duration tracker ting
@Command
public class NowPlayingCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to get the currently playing track.")).queue();
            return;
        }
        final AudioTrack currentTrack = AudioManager.getCurrentlyPlayingTrack(guild);
        if(currentTrack == null) {
            event.replyEmbeds(createMusicReply("There is no song currently playing...")).queue();
            return;
        }
        event.replyEmbeds(getCurrentTrackEmbed(guild, currentTrack)).queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return "Returns the currently playing song.";
    }

    //TODO more info
    private MessageEmbed getCurrentTrackEmbed(Guild guild, AudioTrack currentTrack) {
        AudioTrackInfo currentTrackInfo = currentTrack.getInfo();
        return new EmbedBuilder()
                .setColor(ColorConstants.PRIMARY_COLOR)
                .setAuthor("|  " + "Current Track: " + currentTrackInfo.title + " by " + currentTrackInfo.author, null, "https://avatars.githubusercontent.com/u/65785034?v=4")
                .setDescription("Link: " + currentTrackInfo.uri)
                .setThumbnail(currentTrackInfo.artworkUrl)
                .build();
    }

}
