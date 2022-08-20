package io.github.theblacksquidward.squidwardbot.core.utils;

import java.util.Collection;
import java.util.stream.Collectors;

public class StringUtils {

    public static String getIndentedStringList(Collection<?> collection) {
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\t", "\n\t", ""));
    }

}
