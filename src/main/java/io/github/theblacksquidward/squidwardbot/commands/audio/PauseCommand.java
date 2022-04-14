package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class PauseCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!SquidwardBot.getGuildAudioManager().hasPlayer(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("This guild does not have a player...")).queue();
            return;
        }
        if(AudioUtils.isPlayerPaused(guild)) {
           event.replyEmbeds(EmbedUtils.createMusicReply("The player is already paused...")).queue();
           return;
        }
        AudioUtils.pauseTrack(guild);
        event.replyEmbeds(EmbedUtils.createMusicReply("Successfully paused the player.")).queue();
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pauses the currently playing song.";
    }

}
