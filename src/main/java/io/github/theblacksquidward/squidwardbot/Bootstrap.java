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
        if(version == null) version = "IN-DEV";
        final Dotenv DOTENV = Dotenv.load();
        final Reflections REFLECTIONS = new Reflections("io.github.theblacksquidward");
        LOGGER.info("Starting SquidwardBot (Version {})", version);
        try {
            new SquidwardBot(DOTENV, REFLECTIONS, version);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
