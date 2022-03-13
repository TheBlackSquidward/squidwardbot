package io.github.theblacksquidward.squidwardbot.commands.music;

import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DisconnectCommand implements IGuildCommand {

    //TODO all messages should be embeds
    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(guild.getAudioManager().isConnected()) {
            event.reply("Successfully disconnected from " + guild.getAudioManager().getConnectedChannel().getName() + ".").queue();
            SquidwardBot.getGuildAudioManager().removePlayer(guild);
        } else {
            event.reply("The bot is currently not in a channel.").queue();
        }
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public String getDescription() {
        return "Disconnect the bot from the channel its currently in.";
    }

}
