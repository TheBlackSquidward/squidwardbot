package io.github.theblacksquidward.squidwardbot.core.events;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import io.github.theblacksquidward.squidwardbot.utils.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListenerRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerRegistry.class);
  private static final Map<String, SquidwardBotEventListener> EVENT_LISTENERS = Maps.newHashMap();

  public static void captureAndRegisterEventListeners(Reflections reflections) {
    LOGGER.info("Beginning to scan for event listeners...");
    final Stopwatch timer = Stopwatch.createStarted();
    Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(EventListener.class);
    LOGGER.info(
        "Successfully found "
            + annotatedClasses.size()
            + " event listeners, Attempting to register them...");
    annotatedClasses.forEach(
        (annotatedClass) -> {
          try {
            SquidwardBotEventListener squidwardBotEventListener =
                (SquidwardBotEventListener) annotatedClass.getDeclaredConstructor().newInstance();
            registerEventListener(squidwardBotEventListener);
          } catch (InstantiationException
              | IllegalAccessException
              | InvocationTargetException
              | NoSuchMethodException e) {
            // TODO log better
            e.printStackTrace();
          }
        });
    LOGGER.debug(
        "Loaded event listeners: {}", StringUtils.getIndentedStringList(getEventListeners()));
    timer.stop();
    LOGGER.info(
        "Finished capturing and registering event listeners in {}. Successfully registered {} event listeners.",
        timer,
        getEventListenersSize());
  }

  private static void registerEventListener(SquidwardBotEventListener eventListener) {
    EVENT_LISTENERS.putIfAbsent(eventListener.getEventName(), eventListener);
  }

  public static int getEventListenersSize() {
    return EVENT_LISTENERS.size();
  }

  @UnmodifiableView
  public static Set<SquidwardBotEventListener> getEventListeners() {
    return Set.copyOf(EVENT_LISTENERS.values());
  }

  public void forEachEvent(Consumer<SquidwardBotEventListener> function) {
    getEventListeners().forEach(function);
  }
}
