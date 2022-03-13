package io.github.theblacksquidward.squidwardbot.commands.music;

import io.github.theblacksquidward.squidwardbot.commands.IGuildCommand;
import io.github.theblacksquidward.squidwardbot.utils.AudioUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SkipCommand implements IGuildCommand {

    //TODO bad implementation and all messages should be embeds
    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        AudioUtils.skipTrack(guild);
        event.reply("Skipping track...").queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips the audio track that is currently playing to the next audio track in the queue.";
    }

}
