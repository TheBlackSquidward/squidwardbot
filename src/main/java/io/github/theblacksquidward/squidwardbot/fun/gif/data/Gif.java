package io.github.theblacksquidward.squidwardbot.fun.gif.data;

import com.google.gson.JsonObject;

public class Gif {

  private final String id;
  private final String url;

  public Gif(JsonObject jsonObject) {
    this.id = jsonObject.get("id").getAsString();
    this.url =
        jsonObject.getAsJsonObject("media_formats").getAsJsonObject("gif").get("url").getAsString();
  }

  public String getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }
}
