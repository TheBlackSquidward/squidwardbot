package io.github.theblacksquidward.squidwardbot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.GuildUtils;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;

@CommandController("music")
public class MusicCommand {

    static final EmbedBuilder musicHelpEmbed = new EmbedBuilder()
            .setColor(ColorConstants.MUSIC_COLOR)
            .setTitle("Music Module")
            .setDescription("Here are all the commands from the Music module of SquidwardBot.")
            .addField("Commands",
                    """
                            `help` - Returns the help information for the Music module.\s
                            `disconnect` - Disconnects the bot from the channel it is currently in.\s
                            `skip` - Skips the currently playing track to the next track in the queue.\s
                            `connect <channeL_name>` - Connects the bot to the channel you are currently in or the channel specified.\s
                            `play` - Plays the given song or adds it to the queue if a song is currently playing.\s
                            """, false)
            .setFooter("Coded by TheBlackSquidward", "https://avatars.githubusercontent.com/u/65785034?v=4");

    @Command(isSuper=true)
    public void onMusicCommand(CommandEvent event) {
        event.reply(musicHelpEmbed);
    }

    @Command(value="help")
    public void onMusicHelpCommand(CommandEvent event) {
        event.reply(musicHelpEmbed);
    }

    @Command(value={"leave", "disconnect"})
    public void onMusicDisconnectCommand(CommandEvent event) {
        Guild guild = event.getGuild();
        if(guild.getAudioManager().isConnected()) {
            event.reply("Successfully disconnected from " + guild.getAudioManager().getConnectedChannel().getName() + ".");
            guild.getAudioManager().closeAudioConnection();
        } else {
            event.reply("The bot is not currently in a channel.");
        }
    }

    @Command(value="skip")
    public void onMusicSkipCommand(CommandEvent event) {
        AudioUtils.skipTrack(event.getGuild());
        event.reply("Skipping track...");
    }

    @Command(value = {"join", "connect"})
    public void onMusicConnectCommand(CommandEvent event) {
        Guild guild = event.getGuild();
        User user = event.getAuthor();
        if(guild.getAudioManager().isConnected()) {
            event.reply("The bot is already in the channel: " + guild.getAudioManager().getConnectedChannel().getName());
            return;
        }

        String[] args = ((CommandEvent) event.getCommandContext().getArguments().get(0)).getCommandContext().getInput();
        var string = String.join(" ", Arrays.stream(args).toList());

        // If no args
        if(string.isEmpty()) {
            if(guild.getMember(user) == null) {
                event.reply("You are not in a voice channel silly. Either join a voice channel or specify one.");
                return;
            }
            Member member = guild.getMember(user);
            GuildVoiceState memberVoiceState = member.getVoiceState();
            event.reply("Successfully connected to " + memberVoiceState.getChannel().getName());
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, memberVoiceState.getChannel());
        }else{
            VoiceChannel targetVoiceChannel = GuildUtils.matchVoiceChannel(guild, string);
            if(targetVoiceChannel == null) {
                event.reply("Could not find the given voice channel.");
                return;
            }
            event.reply("Successfully connected to " + targetVoiceChannel.getName());
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, targetVoiceChannel);
        }
    }

    //TODO add msgs
    @Command(value="stop")
    public void onMusicStopCommand(CommandEvent event) {
        Guild guild = event.getGuild();
        if(SquidwardBot.getGuildAudioManager().hasPlayer(guild)) {
            SquidwardBot.getGuildAudioManager().removePlayer(guild);
            event.reply("");
        } else {
            event.reply("");
        }
    }

    @Command(value="queue")
    public void onMusicQueueCommand(CommandEvent event) {

    }

    @Command(value="clear")
    public void onMusicClearCommand(CommandEvent event) {
        Guild guild = event.getGuild();
        if(!SquidwardBot.getGuildAudioManager().hasPlayer(guild)) {
            event.reply("Could not find a player for this guild.");
            return;
        }
        if(SquidwardBot.getGuildAudioManager().getTrackScheduler(event.getGuild()).isQueueEmpty()) {
            event.reply("The music queue is empty...");
        }else{
            SquidwardBot.getGuildAudioManager().removePlayer(guild);
            event.reply("Successfully cleared the music queue.");
        }
    }

    @Command(value="nowplaying")
    public void onMusicNowPlayingCommand(CommandEvent event) {
        Guild guild = event.getGuild();
        if(AudioUtils.getCurrentAudioTrack(guild) == null) {
            event.reply("There is no audio track currently playing.");
            return;
        }
        AudioTrack audioTrack = AudioUtils.getCurrentAudioTrack(guild);
        //TODO this is a proof of concept this should return a more pretty embed
        event.reply(audioTrack.getInfo().title);
    }

    @Command(value="play")
    public void onMusicPlayCommand(CommandEvent event) {
        //TODO here music play should check if the bot is in a channel and join one if not
        Guild guild = event.getGuild();
        String[] args = ((CommandEvent) event.getCommandContext().getArguments().get(0)).getCommandContext().getInput();
        var string = String.join(" ", Arrays.stream(args).toList());
        AudioUtils.loadAndPlay(guild, args[0]);
    }

}
