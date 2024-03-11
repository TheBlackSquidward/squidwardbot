package io.github.theblacksquidward.squidwardbot.rainbowsix.commands;

import io.github.theblacksquidward.squidwardbot.constants.Constants;
import io.github.theblacksquidward.squidwardbot.core.commands.Command;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.rainbowsix.data.RainbowSixEnjoyer;
import java.io.IOException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command
public class StatsCommand extends SquidwardBotCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommand.class);

  @Override
  public void onSlashCommand(SlashCommandInteractionEvent event) {
    final String url = "https://r6stats.com/api/stats/278687de-3832-45d1-85e4-2d46928802d2";
    final String url2 = "https://r6stats.com/api/stats/dad95721-7ec8-4de0-a33f-fb6fd94f3c4f";
    var jsonBrowser = makeRequest(url2);
    RainbowSixEnjoyer rainbowSixEnjoyer = new RainbowSixEnjoyer(jsonBrowser);
    EmbedBuilder embedBuilder = new EmbedBuilder();

    embedBuilder.setTitle(
        rainbowSixEnjoyer.getUsername() + " - " + rainbowSixEnjoyer.getPlatform().toUpperCase());
    embedBuilder.setThumbnail(rainbowSixEnjoyer.getAvatarUrl());
    embedBuilder.setFooter(rainbowSixEnjoyer.getUplayId());
    embedBuilder.setTimestamp(rainbowSixEnjoyer.getLastUpdatedAsInstant());

    embedBuilder.addField("Level", rainbowSixEnjoyer.getLevel(), false);
    embedBuilder.addField("Last Updated", rainbowSixEnjoyer.getTimeSinceLastUpdate(), false);

    embedBuilder.addField("", "**__Overall Stats__**:", false);
    embedBuilder.addField("K/D Ratio", rainbowSixEnjoyer.getKd(), true);
    embedBuilder.addField("Win Rate", rainbowSixEnjoyer.getWinRate(), true);
    embedBuilder.addField("Kills per Match", rainbowSixEnjoyer.getKillsPerMatch(), true);
    embedBuilder.addField("Kills", rainbowSixEnjoyer.getKills(), true);
    embedBuilder.addField("Wins", rainbowSixEnjoyer.getWins(), true);
    embedBuilder.addField("Matches Played", rainbowSixEnjoyer.getMatchesPlayed(), true);
    embedBuilder.addField("Deaths", rainbowSixEnjoyer.getDeaths(), true);
    embedBuilder.addField("Losses", rainbowSixEnjoyer.getLosses(), true);
    embedBuilder.addField("Playtime", rainbowSixEnjoyer.getPrettyPlaytime(), true);

    event.replyEmbeds(embedBuilder.build()).queue();
  }

  @Override
  public String getName() {
    return "stats";
  }

  @Override
  public String getDescription() {
    // TODO
    return "Get Stats";
  }

  private JsonBrowser makeRequest(String url) {
    Request request = new Request.Builder().url(url).build();
    try (Response response = Constants.OK_HTTP_CLIENT.newCall(request).execute()) {
      return JsonBrowser.parse(response.body().string());
    } catch (IOException exception) {
      LOGGER.error(exception.getMessage());
    }
    // TODO
    return null;
  }
}
