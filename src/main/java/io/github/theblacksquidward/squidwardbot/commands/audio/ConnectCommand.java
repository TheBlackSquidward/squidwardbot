package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@Command
public class ConnectCommand extends AbstractAudioCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        OptionMapping option = event.getOption("channel");
        event.deferReply().queue();
        if(event.getGuild().getAudioManager().isConnected()) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot is already connected to the channel" + guild.getAudioManager().getConnectedChannel().getName())).queue();
            return;
        }
        if(option == null) {
            if(guild.getMember(user) == null) {
                event.getHook().sendMessageEmbeds(createMusicReply("You are not currently in a voice channel. Either join one or specify one.")).queue();
                return;
            }
            final AudioChannel audioChannel = guild.getMember(user).getVoiceState().getChannel();
            guild.getAudioManager().openAudioConnection(audioChannel);
            event.getHook().sendMessageEmbeds(createMusicReply("Successfully connected to " + audioChannel.getName())).queue();
        } else {
            GuildChannelUnion channel = option.getAsChannel();
            if(channel.getType() != ChannelType.VOICE) {
                event.getHook().sendMessageEmbeds(createMusicReply("The channel specified is not a voice channel.")).queue();
                return;
            }
            VoiceChannel voiceChannel = channel.asVoiceChannel();
            guild.getAudioManager().openAudioConnection(voiceChannel);
            event.getHook().sendMessageEmbeds(createMusicReply("Successfully connected to " + voiceChannel.getName())).queue();
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
    public List<OptionData> getOptionData() {
        return List.of(new OptionData(OptionType.CHANNEL, "channel", "The channel the bot should be connected to", false));
    }

}
