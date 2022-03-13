package io.github.theblacksquidward.squidwardbot.commands.music;

import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ClearCommand implements IGuildCommand {

    //TODO all messages should be embeds
    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!SquidwardBot.getGuildAudioManager().hasPlayer(guild)) {
            event.reply("Could not find a player for this guild.").queue();
            return;
        }
        if(SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).isQueueEmpty()) {
            event.reply("The queue is currently empty... Theres nothing to skip.").queue();
        } else {
            SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).clearQueue();
            event.reply("Successfully cleared the queue.").queue();
        }
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears the current audio track queue.";
    }

}
