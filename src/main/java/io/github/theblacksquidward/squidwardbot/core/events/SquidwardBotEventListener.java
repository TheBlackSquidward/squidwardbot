package io.github.theblacksquidward.squidwardbot.core.events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class SquidwardBotEventListener extends ListenerAdapter {

  public String getEventName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public String toString() {
    return getEventName();
  }
}
