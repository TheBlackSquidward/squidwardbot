package io.github.theblacksquidward.squidwardbot.utils;

import java.awt.*;

public class ColorUtils {

    public static String toHexString(Color color) {
        if (color == null) return "#000000";
        return  "#" + String.format("%02x", color.getRed()) +
                String.format("%02x", color.getGreen()) +
                String.format("%02x", color.getBlue());
    }

}
