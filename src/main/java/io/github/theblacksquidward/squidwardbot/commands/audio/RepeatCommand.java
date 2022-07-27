package io.github.theblacksquidward.squidwardbot.commands.audio;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import io.github.theblacksquidward.squidwardbot.commands.Command;
import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import io.github.theblacksquidward.squidwardbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command
public class RepeatCommand implements IGuildCommand {

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!AudioUtils.hasPlayer(guild)) {
            event.replyEmbeds(EmbedUtils.createMusicReply("This guild does not have a player...")).queue();
            return;
        }
        if(AudioUtils.getCurrentAudioTrack(guild) == null) {
            event.replyEmbeds(EmbedUtils.createMusicReply("There is no song currently playing.")).queue();
            return;
        }
        SquidwardBot.getGuildAudioManager().getTrackScheduler(guild).addTrackAtHead(AudioUtils.getCurrentAudioTrack(guild).makeClone());
        event.replyEmbeds(EmbedUtils.createMusicReply("Successfully requeued the currently playing the song.")).queue();
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getDescription() {
        return "Repeats the currently playing track.";
    }

}
