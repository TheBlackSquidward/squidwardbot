package io.github.theblacksquidward.squidwardbot.core.constants;

import java.awt.*;

public class ColorConstants {

    public static final Color PRIMARY_COLOR = new Color(5, 224, 250);
    public static final Color SECONDARY_COLOR = new Color(250, 197, 5);
    public static final Color TERTIARY_COLOR = new Color(250, 5, 115);

    public static final Color GREEN_COLOR = new Color(0,255,0);
    public static final Color RED_COLOR = new Color(255,0,0);

    public static Color getRandomColor() {
        return new Color(Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat());
    }

}
