package io.github.theblacksquidward.squidwardbot.commands.music;

import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class ConnectCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        OptionMapping option = event.getOption("channel");
        if(guild.getAudioManager().isConnected()) {
            event.reply("The bot is already in the channel: " + guild.getAudioManager().getConnectedChannel().getName()).queue();
            return;
        }
        if(option == null) {
            if(guild.getMember(user) == null) {
                event.reply("You are not in a voice channel silly. Either join a voice channel or specify one.");
                return;
            }
            Member member = guild.getMember(user);
            GuildVoiceState memberVoiceState = member.getVoiceState();
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, memberVoiceState.getChannel());
            event.reply("Successfully connected to " + memberVoiceState.getChannel().getName()).queue();
        } else {
            VoiceChannel voiceChannel = option.getAsVoiceChannel();
            if(voiceChannel == null) {
                event.reply("The channel specified is not a voice channel.").queue();
                return;
            }
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, voiceChannel);
            event.reply("Successfully connected to " + voiceChannel.getName()).queue();
        }
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public String getDescription() {
        return "Connects the bot to a channel.";
    }

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl(getName(), getDescription())
                .addOption(OptionType.CHANNEL, "channel", "The channel the bot should be connected to", false);
    }

}
