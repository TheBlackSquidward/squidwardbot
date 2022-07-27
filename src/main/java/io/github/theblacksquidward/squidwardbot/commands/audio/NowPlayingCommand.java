package io.github.theblacksquidward.squidwardbot.commands.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

//TODO should have a duration tracker ting
@Command
public class NowPlayingCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!SquidwardBot.getGuildAudioManager().hasPlayer(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("This guild does not have a player...")).queue();
            return;
        }
        AudioTrack currentTrack = AudioUtils.getCurrentAudioTrack(guild);
        if(currentTrack == null) {
            event.replyEmbeds(EmbedUtils.createMusicReply("There is no song currently playing...")).queue();
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
