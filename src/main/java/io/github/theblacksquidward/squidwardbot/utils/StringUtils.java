package io.github.theblacksquidward.squidwardbot.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

  public static String getIndentedStringList(Collection<?> collection) {
    return collection.stream()
        .map(Object::toString)
        .collect(Collectors.joining("\n\t", "\n\t", ""));
  }

  public static String millisecondsFormatted(final long millis) {
    final long hours =
        TimeUnit.MILLISECONDS.toHours(millis)
            - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
    final long minutes =
        TimeUnit.MILLISECONDS.toMinutes(millis)
            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
    final long seconds =
        TimeUnit.MILLISECONDS.toSeconds(millis)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
    final String ret =
        String.format(
                "%s%s%s",
                hours > 0 ? String.format("%02d", hours) + ":" : "",
                minutes > 0 ? String.format("%02d", minutes) + ":" : "00:",
                seconds > 0 ? String.format("%02d", seconds) : "00")
            .trim();
    return ret.endsWith(":") ? ret.substring(0, ret.length() - 1) : ret;
  }

  public static boolean startsWithAny(
      final @NotNull String input, final @NotNull String... searchStrings) {
    if (input.isEmpty()) return false;
    return Arrays.stream(searchStrings).anyMatch(input::startsWith);
  }

  public static String formatTime(OffsetDateTime time) {
    return time.format(DateTimeFormatter.RFC_1123_DATE_TIME);
  }
}
