package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Command
public class ConnectCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        OptionMapping option = event.getOption("channel");
        if(guild.getAudioManager().isConnected()) {
            event.replyEmbeds(EmbedUtils.createMusicReply("The bot is already connected to the channel" + guild.getAudioManager().getConnectedChannel().getName())).queue();
            return;
        }
        if(option == null) {
            if(guild.getMember(user) == null) {
                event.replyEmbeds(EmbedUtils.createMusicReply("You are not currently in a voice channel. Either join one or specifiy one.")).queue();
                return;
            }
            Member member = guild.getMember(user);
            GuildVoiceState memberVoiceState = member.getVoiceState();
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, memberVoiceState.getChannel());
            event.replyEmbeds(EmbedUtils.createMusicReply("Successfully connected to " + memberVoiceState.getChannel().getName())).queue();
        } else {
            GuildChannelUnion channel = option.getAsChannel();
            if(channel.getType() != ChannelType.VOICE) {
                event.replyEmbeds(EmbedUtils.createMusicReply("The channel specified is not a voice channel.")).queue();
                return;
            }
            VoiceChannel voiceChannel = channel.asVoiceChannel();
            SquidwardBot.getGuildAudioManager().openAudioConnection(guild, voiceChannel);
            event.replyEmbeds(EmbedUtils.createMusicReply("Successfully connected to " + voiceChannel.getName())).queue();
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
