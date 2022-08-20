package io.github.theblacksquidward.squidwardbot.audio.commands;

import genius.SongSearch;
import genius.SongSearch.Hit;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Command
public class LyricsCommand extends AbstractAudioCommand{

    private static final Map<Long, String> ID_LYRIC_MAP = new HashMap<>();

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.replyEmbeds(createMusicReply("The bot must be connected to a voice channel to pause the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.replyEmbeds(createMusicReply("You must be in the same voice channel as the bot to pause the queue.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.replyEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        try {
            final SongSearch songSearch = AudioManager.getLyrics(guild);
            final LinkedList<Hit> hits = songSearch.getHits();
            if(hits.isEmpty()) {
                event.replyEmbeds(createMusicReply("There are no lyric results for this song.")).queue();
                return;
            }
            final Hit hit = hits.getFirst();
            event.replyEmbeds(getLyricsEmbed(hit)).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "lyrics";
    }

    @Override
    public String getDescription() {
        return "Returns the lyrics of the currently playing song.";
    }

    private MessageEmbed getLyricsEmbed(Hit hit) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(hit.getThumbnailUrl());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
        embedBuilder.setFooter(hit.getArtist().getName(), hit.getArtist().getImageUrl());
        embedBuilder.setTitle(hit.getTitleWithFeatured());
        final String lyrics = hit.fetchLyrics();
        embedBuilder.setDescription(lyrics.substring(0, Math.min(lyrics.length(), 1024)));
        return embedBuilder.build();
    }

}
