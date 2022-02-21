package io.github.theblacksquidward.squidwardbot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Arrays;

@CommandController("music")
public class MusicCommand {

    static final EmbedBuilder musicHelpEmbed = new EmbedBuilder()
            .setColor(Color.MAGENTA)
            .setTitle("Music Module")
            .setDescription("Here are all the commands from the Music module of SquidwardBot.")
            .addField("Commands",
                    """
                            `help` - Returns the help information for the Music module.\s
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
        AudioManager.skipTrack(event.getGuild());
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
            event.getGuild().getAudioManager().setSelfDeafened(true);
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        }else{
            //TODO will give null pointer as not implemented
            // If channel give
            VoiceChannel targetVoiceChannel = null;
            event.reply("Successfully connected to " + targetVoiceChannel.getName());
            event.getGuild().getAudioManager().setSelfDeafened(true);
            event.getGuild().getAudioManager().openAudioConnection(targetVoiceChannel);
        }
    }

    @Command(value="stop")
    public void onMusicStopCommand(CommandEvent event) {

    }

    @Command(value="queue")
    public void onMusicQueueCommand(CommandEvent event) {

    }

    @Command(value="clear")
    public void onMusicClearCommand(CommandEvent event) {

    }

    @Command(value="nowplaying")
    public void onMusicNowPlayingCommand(CommandEvent event) {
    }

    @Command(value="play")
    public void onMusicPlayCommand(CommandEvent event) {
        Guild guild = event.getGuild();
        String[] args = ((CommandEvent) event.getCommandContext().getArguments().get(0)).getCommandContext().getInput();
        var string = String.join(" ", Arrays.stream(args).toList());
        AudioManager.loadAndPlay(guild, args[0]);
        guild.getAudioManager().openAudioConnection(guild.getAudioManager().getConnectedChannel());
    }

}