package io.github.theblacksquidward.squidwardbot.core;

import com.google.common.collect.Sets;
import java.util.Set;

public class ShutdownHooks {

  private static final Set<Runnable> HOOKS = Sets.newHashSet();

  public static void register(Runnable runnable) {
    HOOKS.add(runnable);
  }

  public static void runShutdownHooks() {
    HOOKS.forEach(Runnable::run);
  }
}
