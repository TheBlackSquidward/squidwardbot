package io.github.theblacksquidward.squidwardbot.fun.gif.data;

import com.google.gson.JsonObject;
import io.github.theblacksquidward.squidwardbot.constants.Constants;
import java.util.LinkedList;

public class GifSearch {

  final String next;

  final LinkedList<Gif> gifs = new LinkedList<>();

  public GifSearch(JsonObject jsonObject) {
    this.next = jsonObject.get("next").getAsString();
    jsonObject
        .get("results")
        .getAsJsonArray()
        .forEach(jsonElement -> this.gifs.add(new Gif(jsonElement.getAsJsonObject())));
  }

  public String getNext() {
    return next;
  }

  public LinkedList<Gif> getGifs() {
    return gifs;
  }

  public Gif getRandomGif() {
    return gifs.get(Constants.RANDOM.nextInt(gifs.size()));
  }
}
