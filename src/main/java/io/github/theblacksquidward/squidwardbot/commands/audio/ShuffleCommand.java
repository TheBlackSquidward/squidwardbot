package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class ShuffleCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!SquidwardBot.getGuildAudioManager().hasPlayer(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("Could not find a player for this guild.")).queue();
            return;
        }
        if(AudioUtils.isQueueEmpty(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("Could not shuffle the queue as it is empty.")).queue();
            return;
        }
        AudioUtils.shuffleQueue(guild);
        event.replyEmbeds(EmbedUtils.createMusicReply("Successfully shuffled the queue.")).queue();
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "Shuffles the current queue.";
    }

}
