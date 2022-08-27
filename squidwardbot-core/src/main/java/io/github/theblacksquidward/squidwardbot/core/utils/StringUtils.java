package io.github.theblacksquidward.squidwardbot.core.utils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StringUtils {

    public static String getIndentedStringList(Collection<?> collection) {
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\t", "\n\t", ""));
    }

    //TODO
    public static String millisecondsFormatted(final long millis) {
        final long hours = TimeUnit.MILLISECONDS.toHours(millis)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        final String ret = String.format("%s%s%s", hours > 0 ? String.format("%02d", hours) + ":" : "",
                minutes > 0 ? String.format("%02d", minutes) + ":" : "00:",
                seconds > 0 ? String.format("%02d", seconds) : "00").trim();
        return ret.endsWith(":") ? ret.substring(0, ret.length() - 1) : ret;
    }

}
