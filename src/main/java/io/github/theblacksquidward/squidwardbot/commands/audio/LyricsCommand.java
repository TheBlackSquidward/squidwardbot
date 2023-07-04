package io.github.theblacksquidward.squidwardbot.commands.audio;

import genius.SongSearch;
import genius.SongSearch.Hit;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Command
public class LyricsCommand extends AbstractAudioCommand{

    private static final Map<Long, Hit> ID_LYRIC_MAP = new HashMap<>();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(!event.isFromGuild()) return;
        if(!event.getComponentId().startsWith("lyric")) return;
        String componentId = event.getComponentId();
        String[] componentIdArray = componentId.split("_");

        final String buttonId = componentIdArray[0];
        final long channelId = Long.parseLong(componentIdArray[1]);
        final long userId = Long.parseLong(componentIdArray[2]);
        final long hitId = Long.parseLong(componentIdArray[3]);
        final int oldPage = Integer.parseInt(componentIdArray[4].replace("page", ""));
        final String instruction = componentIdArray[5];

        int newPage = oldPage;
        if(instruction.equalsIgnoreCase("close")) {
            event.deferEdit().flatMap(InteractionHook::deleteOriginal).queue();
            ID_LYRIC_MAP.remove(hitId);
        }
        final Hit hit = ID_LYRIC_MAP.get(hitId);
        final String lyrics = hit.fetchLyrics();
        if(instruction.equalsIgnoreCase("next")) {
            newPage++;
            final List<String> requestedLyrics = getLyricsPage(lyrics.lines().collect(Collectors.toList()), newPage);
            if(requestedLyrics.isEmpty()) {
                event.editButton(event.getButton().withDisabled(true)).queue();
                return;
            }
            final String nextPage = String.join("\n", requestedLyrics);
            final ActionRow actionRow = event.getMessage().getActionRows().get(0);
            Button previous = actionRow.getButtons().get(0);
            Button close = actionRow.getButtons().get(1);
            Button next = actionRow.getButtons().get(2);
            previous = previous.withId(buttonId + "_" + channelId + "_" + userId + "_" + hitId + "_" + newPage + "_prev").withDisabled(false);
            close = close.withId(buttonId + "_" + channelId + "_" + userId + "_" + hitId + "_" + newPage + "_close").withDisabled(false);
            next = next.withId(buttonId + "_" + channelId + "_" + userId + "_" + hitId + "_" + newPage + "_next").withDisabled(false);
            event.editMessageEmbeds(
                    new EmbedBuilder(event.getMessage().getEmbeds().get(0)).setDescription(nextPage).build())
                    .setComponents(ActionRow.of(previous, close, next)).queue();
        }
        if(instruction.equalsIgnoreCase("prev")) {
            newPage--;
            final List<String> requestedLyrics = getLyricsPage(lyrics.lines().collect(Collectors.toList()), newPage);
            if(requestedLyrics.isEmpty()) {
                event.editButton(event.getButton().withDisabled(true)).queue();
                return;
            }
            final String nextPage = String.join("\n", requestedLyrics);
            final ActionRow actionRow = event.getMessage().getActionRows().get(0);
            Button previous = actionRow.getButtons().get(0);
            Button close = actionRow.getButtons().get(1);
            Button next = actionRow.getButtons().get(2);
            previous = previous.withId(buttonId + "_" + channelId + "_" + userId + "_" + hitId + "_" + newPage + "_prev").withDisabled(false);
            close = close.withId(buttonId + "_" + channelId + "_" + userId + "_" + hitId + "_" + newPage + "_close").withDisabled(false);
            next = next.withId(buttonId + "_" + channelId + "_" + userId + "_" + hitId + "_" + newPage + "_next").withDisabled(false);
            event.editMessageEmbeds(
                            new EmbedBuilder(event.getMessage().getEmbeds().get(0)).setDescription(nextPage).build())
                    .setComponents(ActionRow.of(previous, close, next)).queue();
        }
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        event.deferReply().queue();
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in a voice channel to use this command.")).queue();
            return;
        }
        final AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if(!event.getGuild().getAudioManager().isConnected()) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot must be connected to a voice channel to pause the queue.")).queue();
            return;
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to pause the queue.")).queue();
            return;
        }
        if(!AudioManager.isPlayingTrack(guild)) {
            event.getHook().sendMessageEmbeds(createMusicReply("The bot is not currently playing anything...")).queue();
            return;
        }
        try {
            final SongSearch songSearch = AudioManager.getLyrics(guild);
            final LinkedList<Hit> hits = songSearch.getHits();
            if(hits.isEmpty()) {
                event.getHook().sendMessageEmbeds(createMusicReply("There are no lyric results for this song.")).queue();
                return;
            }
            final Hit hit = hits.getFirst();
            ID_LYRIC_MAP.put(hit.getId(), hit);
            event.getHook().sendMessageEmbeds(getLyricsEmbed(hit))
                    .setComponents(ActionRow.of(
                            Button.primary("lyrics_" + event.getChannel().getId() + "_" + event.getUser().getId() + "_" + hit.getId() + "_page0" + "_prev", Emoji.fromUnicode("◀️"))
                                    .asDisabled(),
                            Button.danger("lyrics_" + event.getChannel().getId() + "_" + event.getUser().getId() + "_" + hit.getId() + "_page0" + "_close", Emoji.fromUnicode("⚠️")),
                            Button.primary("lyrics_" + event.getChannel().getId() + "_" + event.getUser().getId() + "_" + hit.getId() + "_page0" + "_next", Emoji.fromUnicode("▶️"))
                    )).queue();
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
        embedBuilder.setTitle(hit.getTitleWithFeatured(), hit.getUrl());
        embedBuilder.setDescription(String.join("\n", getLyricsPage(hit.fetchLyrics().lines().collect(Collectors.toList()), 0)));
        return embedBuilder.build();
    }

    private List<String> getLyricsPage(List<String> lines, int page) {
        int minBound = 1024 * page;
        int maxBound = 1024 * (1 + page);
        int characterCounter = 0;
        var output = new ArrayList<String>();
        for (String line : lines) {
            characterCounter += line.length();
            if(maxBound < characterCounter) {
                break;
            }
            if(minBound < characterCounter) {
                output.add(line);
            }
        }
        return output;
    }

}
