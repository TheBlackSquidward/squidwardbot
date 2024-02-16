package io.github.theblacksquidward.squidwardbot.fun.commands;

import io.github.theblacksquidward.squidwardbot.Environment;
import io.github.theblacksquidward.squidwardbot.constants.Constants;
import io.github.theblacksquidward.squidwardbot.core.commands.SquidwardBotCommand;
import io.github.theblacksquidward.squidwardbot.fun.gif.TenorAPI;
import net.dv8tion.jda.api.EmbedBuilder;

public abstract class AbstractGifCommand extends SquidwardBotCommand {

  private static final TenorAPI tenorAPI =
      new TenorAPI(Constants.OK_HTTP_CLIENT, Environment.getInstance().getTenorApiKey());

  public EmbedBuilder getGifEmbedBuilder(String query) {
    return new EmbedBuilder().setImage(tenorAPI.search(query).getRandomGif().getUrl());
  }

  public EmbedBuilder getLonelyEmbedBuilder() {
    return getGifEmbedBuilder("anime lonely");
  }

  public String getRandomMessage(String[] messages) {
    int random = Constants.RANDOM.nextInt(messages.length);
    return messages[random];
  }
}
