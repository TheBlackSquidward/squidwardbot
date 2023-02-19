package io.github.theblacksquidward.squidwardbot.audio.commands;

import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.DefaultYoutubeLinkRouter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeClientConfig;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeConstants;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylistInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.audio.BaseAudioLoadResultImpl;
import io.github.theblacksquidward.squidwardbot.audio.TrackScheduler;
import io.github.theblacksquidward.squidwardbot.audio.source.applemusic.AppleMusicSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.deezer.DeezerAudioSourceManager;
import io.github.theblacksquidward.squidwardbot.audio.source.spotify.SpotifySourceManager;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.constants.ColorConstants;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Command
public class PlayCommand extends AbstractAudioCommand {

    private static final String[] PREFIXES = {
            DefaultYoutubeLinkRouter.SEARCH_PREFIX,
            DefaultYoutubeLinkRouter.SEARCH_MUSIC_PREFIX,
            SoundCloudAudioSourceManager.SEARCH_PREFIX,
            SpotifySourceManager.RECOMMENDATIONS_PREFIX,
            SpotifySourceManager.SEARCH_PREFIX,
            AppleMusicSourceManager.SEARCH_PREFIX,
            DeezerAudioSourceManager.SEARCH_PREFIX,
            DeezerAudioSourceManager.ISRC_PREFIX,
            "https://",
            "www."
    };

    private static final String QUERY_ID = "query";

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equalsIgnoreCase(getName())) {
            String query = event.getOption(QUERY_ID).getAsString();
            if (query.equalsIgnoreCase("")) return;
            if (StringUtils.startsWithAny(query, PREFIXES)) return;

            List<net.dv8tion.jda.api.interactions.commands.Command.Choice> choices = new ArrayList<>();

            HttpInterfaceManager httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();
            HttpPost post = new HttpPost(YoutubeConstants.MUSIC_SEARCH_URL);
            YoutubeClientConfig clientConfig = YoutubeClientConfig.MUSIC.copy()
                    .withRootField("query", query)
                    .setAttribute(httpInterfaceManager.getInterface());
            StringEntity payload = new StringEntity(clientConfig.toJsonString(), "UTF-8");
            post.setHeader("Referer", "music.youtube.com");
            post.setEntity(payload);
            try (CloseableHttpResponse response = httpInterfaceManager.getInterface().execute(post)) {
                HttpClientTools.assertSuccessWithContent(response, "search music response");
                HttpClientTools.assertJsonContentType(response);

                String responseText = EntityUtils.toString(response.getEntity(), UTF_8);

                JsonBrowser jsonBrowser = JsonBrowser.parse(responseText);
                if(jsonBrowser.isNull()) return;

                jsonBrowser.get("contents")
                        .get("tabbedSearchResultsRenderer")
                        .get("tabs")
                        .index(0)
                        .get("tabRenderer")
                        .get("content")
                        .get("sectionListRenderer")
                        .get("contents").values().stream()
                        .map(json -> json.get("musicShelfRenderer"))
                        .filter(json -> !json.get("title").get("runs").index(0).get("text").text().equalsIgnoreCase("Community playlists"))
                        .forEach(json -> json.get("contents").values().forEach(json1 -> {
                            JsonBrowser musicResponsiveListItemRenderer = json1.get("musicResponsiveListItemRenderer");
                            List<JsonBrowser> flexColumns = musicResponsiveListItemRenderer.get("flexColumns").values();
                            JsonBrowser firstColumn = flexColumns.get(0);
                            List<JsonBrowser> metadata = flexColumns.get(1).get("musicResponsiveListItemFlexColumnRenderer").get("text").get("runs").values();

                            String title = firstColumn.get("musicResponsiveListItemFlexColumnRenderer").get("text").get("runs").index(0).get("text").text();
                            String artist = metadata.get(2).get("text").text();
                            String type = metadata.get(0).get("text").text();
                            String name = title + " - " + artist + " (" + type + ")";

                            String value = "";
                            String videoId = firstColumn.get("musicResponsiveListItemFlexColumnRenderer").get("text").get("runs").index(0)
                                    .get("navigationEndpoint").get("watchEndpoint").get("videoId").text();
                            String browseId = musicResponsiveListItemRenderer.get("navigationEndpoint").get("browseEndpoint").get("browseId").text();
                            String playlistId = musicResponsiveListItemRenderer.get("overlay").get("musicItemThumbnailOverlayRenderer").get("content").get("musicPlayButtonRenderer")
                                        .get("playNavigationEndpoint").get("watchPlaylistEndpoint").get("playlistId").text();

                            if (videoId != null) value = YoutubeConstants.WATCH_URL_PREFIX + videoId;
                            else if (browseId != null && browseId.startsWith("UC")) value = YoutubeConstants.MUSIC_CHANNEL_URL_PREFIX + browseId;
                            else if (playlistId != null) value = YoutubeConstants.MUSIC_PLAYLIST_URL_PREFIX + playlistId;

                            //TODO: Implement a way to send all give choices irrespective of size.
                            if (name.length() < 100) choices.add(new net.dv8tion.jda.api.interactions.commands.Command.Choice(name, value));
                        }));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            event.replyChoices(choices).queue();
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
            guild.getAudioManager().openAudioConnection(audioChannel);
            event.getHook().sendMessageEmbeds(createMusicReply("Successfully connected to " + audioChannel.getName())).queue();
        }
        if(event.getMember().getVoiceState().getChannel().getIdLong() != audioChannel.getIdLong()) {
            event.getHook().sendMessageEmbeds(createMusicReply("You must be in the same voice channel as the bot to add an audio track to the queue.")).queue();
            return;
        }
        final String identifier = event.getOption(QUERY_ID).getAsString();
        AudioManager.loadAndPlay(guild, identifier, new AudioLoadResultImpl(event, identifier, AudioManager.getOrCreate(guild).getTrackScheduler()));
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays the given song.";
    }

    @Override
    public List<OptionData> getOptionData() {
        return List.of(
                new OptionData(OptionType.STRING, QUERY_ID, "Identifier of the track (URL/Name)", true, true)
        );
    }

    private static class AudioLoadResultImpl extends BaseAudioLoadResultImpl {

        private final SlashCommandInteractionEvent event;
        private final String identifier;

        public AudioLoadResultImpl(SlashCommandInteractionEvent event, String identifier, TrackScheduler trackScheduler) {
            super(trackScheduler);
            this.event = event;
            this.identifier = identifier;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            trackScheduler.queueTrack(audioTrack);
            AudioTrackInfo audioTrackInfo = audioTrack.getInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setDescription("Successfully queued the track " + audioTrackInfo.title + " by " + audioTrackInfo.author + " [" + StringUtils.millisecondsFormatted(audioTrack.getDuration()) + "] at position #" + AudioManager.getPositionInQueue(event.getGuild(), audioTrack) + " in the queue.");
            embedBuilder.setThumbnail(audioTrackInfo.artworkUrl);
            embedBuilder.setFooter(audioTrackInfo.author, audioTrackInfo.authorArtworkUrl);
            embedBuilder.setTitle(audioTrackInfo.title, audioTrackInfo.uri);
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            super.trackLoaded(audioTrack);
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            audioPlaylist.getTracks().forEach(trackScheduler::queueTrack);
            AudioPlaylistInfo audioPlaylistInfo = audioPlaylist.getAudioPlaylistInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            List<String> formattedPlaylistTracks = audioPlaylist.getTracks().stream()
                    .map(audioTrack -> audioTrack.getInfo().title + " by " + audioTrack.getInfo().author)
                    .toList();
            embedBuilder.setTimestamp(Instant.now());
            embedBuilder.setColor(ColorConstants.PRIMARY_COLOR);
            embedBuilder.setDescription(StringUtils.getIndentedStringList(formattedPlaylistTracks));
            // embedBuilder.setThumbnail(audioPlaylistInfo.artworkUrl());
            // embedBuilder.setFooter(audioPlaylistInfo.owner(), audioPlaylistInfo.ownerThumbnailUrl());
            embedBuilder.setFooter(audioPlaylistInfo.getOwner());
            embedBuilder.setTitle(audioPlaylistInfo.getName(), audioPlaylistInfo.getUri());
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            super.playlistLoaded(audioPlaylist);
        }

        @Override
        public void noMatches() {
            event.getHook().sendMessageEmbeds(createMusicReply("Could not match the given identifier: `" + identifier + "` to an audio track or an audio playlist.")).queue();
            super.noMatches();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            event.getHook().sendMessageEmbeds(createMusicReply(
                    "Error whilst queueing the track/playlist. Please report the following information:" +
                            "\n\nSeverity: " + exception.severity.name() +
                            "\nSpecified Identifier: " + identifier +
                            "\nException: " + exception.getMessage())).queue();
            super.loadFailed(exception);
        }

    }

}
