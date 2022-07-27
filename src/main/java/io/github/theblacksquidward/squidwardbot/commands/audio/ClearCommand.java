package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ClearCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!AudioUtils.hasPlayer(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("Could not find a player for this guild.")).queue();
            return;
        }
        if(AudioUtils.isQueueEmpty(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("The queue is currently empty... There is nothing to skip.")).queue();
            return;
        }
        SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).clearQueue();
        event.replyEmbeds(EmbedUtils.createMusicReply("Successfully cleared the queue.")).queue();
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
