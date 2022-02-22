package io.github.theblacksquidward.squidwardbot.commands;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import io.github.theblacksquidward.squidwardbot.utils.constants.ColorConstants;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@CommandController(value = {"minecraft", "mc"})
public class MinecraftCommand {

    static final EmbedBuilder minecraftHelpEmbed = new EmbedBuilder()
            .setTitle("Minecraft Commands")
            .setColor(ColorConstants.MINECRAFT_COLOR)
            .appendDescription("Here all the commands from the Minecraft module of the SquidwardBot")
            .addField("Commands",
                    """
                            `help` - Returns the help information for the Minecraft module.\s
                            `get <version>` - Returns information about the specified version.\s
                            `latest` - Returns information about the latest version of Minecraft.\s
                            `snapshot` - Returns information about the latest snapshot version of Minecraft.\s
                            """, false)
            .setThumbnail("https://cdn.freebiesupply.com/logos/large/2x/minecraft-1-logo-png-transparent.png")
            .setFooter("Coded by TheBlackSquidward", "https://avatars.githubusercontent.com/u/65785034?v=4");

    @Command(isSuper = true)
    public void onMinecraftCommand(CommandEvent event) {
        event.reply(minecraftHelpEmbed);
    }

    @Command(value="help")
    public void onMinecraftHelpCommand(CommandEvent event) {
        event.reply(minecraftHelpEmbed);
    }

    @Command(value="get")
    public void onMinecraftGetCommand(CommandEvent event) {

    }

}
