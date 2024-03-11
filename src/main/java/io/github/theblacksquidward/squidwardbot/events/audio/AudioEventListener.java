package io.github.theblacksquidward.squidwardbot.events.audio;

import io.github.theblacksquidward.squidwardbot.audio.AudioManager;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public class AudioEventListener extends SquidwardBotEventListener {

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    AudioManager.init();
  }
}
