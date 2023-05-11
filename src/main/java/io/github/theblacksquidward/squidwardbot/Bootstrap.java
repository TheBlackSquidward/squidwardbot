package io.github.theblacksquidward.squidwardbot;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        String version = Bootstrap.class.getPackage().getImplementationVersion();
        final boolean inDev = version == null;
        final Dotenv dotenv = Dotenv.load();
        final Reflections reflections = new Reflections("io.github.theblacksquidward");
        try {
            if (inDev) version = "IN-DEV";
            LOGGER.info("Starting SquidwardBot (Version {})", version);
            new SquidwardBot(inDev, dotenv, reflections, version);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
