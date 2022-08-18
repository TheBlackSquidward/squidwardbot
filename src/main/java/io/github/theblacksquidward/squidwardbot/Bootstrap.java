package io.github.theblacksquidward.squidwardbot;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        OptionSpec<String> optionSpec = optionParser.accepts("discordBotAccessToken").withRequiredArg().required();
        OptionSpec<String> optionSpec1 = optionParser.accepts("version").withRequiredArg().required();
        OptionSpec<String> optionSpec2 = optionParser.accepts("spotifyClientId").withRequiredArg().required();
        OptionSpec<String> optionSpec3 = optionParser.accepts("spotifyClientSecret").withRequiredArg().required();
        OptionSpec<String> nonOptions = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        List<String> list  = optionSet.valuesOf(nonOptions);
        if(!list.isEmpty()) {
            LOGGER.warn("Completely ignored arguments: " + list);
        }
        String discordBotAccessToken = parseArgument(optionSet, optionSpec);
        String version = parseArgument(optionSet, optionSpec1);
        String spotifyClientId = parseArgument(optionSet, optionSpec2);
        String spotifyClientSecret = parseArgument(optionSet, optionSpec3);
        final Reflections REFLECTIONS = new Reflections("io.github.theblacksquidward");
        LOGGER.info("Starting SquidwardBot v{}", version);
        final SquidwardBot SQUIDWARD_BOT;
        try {
            SQUIDWARD_BOT = new SquidwardBot(discordBotAccessToken, REFLECTIONS, version, spotifyClientId, spotifyClientSecret);
        } catch (LoginException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T parseArgument(OptionSet optionSet, OptionSpec<T> optionSpec) {
        try {
            return optionSet.valueOf(optionSpec);
        } catch (Throwable throwable) {
            if (optionSpec instanceof ArgumentAcceptingOptionSpec<T> argumentAcceptingOptionSpec) {
                List<T> list = argumentAcceptingOptionSpec.defaultValues();
                if (!list.isEmpty()) {
                    return list.get(0);
                }
            }
            throw throwable;
        }
    }

}
