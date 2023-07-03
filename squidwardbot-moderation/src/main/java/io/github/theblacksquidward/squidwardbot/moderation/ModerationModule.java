package io.github.theblacksquidward.squidwardbot.moderation;

import io.github.theblacksquidward.squidwardbot.core.commands.CommandRegistry;
import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.moderation.commands.DeafeanCommand;
import io.github.theblacksquidward.squidwardbot.moderation.commands.UndeafenCommand;
import io.github.theblacksquidward.squidwardbot.moderation.commands.UnnickCommand;
import io.github.theblacksquidward.squidwardbot.moderation.event.WelcomeMessageEventHandler;
import io.github.theblacksquidward.squidwardbot.moderation.event.logging.global.*;
import io.github.theblacksquidward.squidwardbot.moderation.event.logging.guild.GuildChannelUpdateEventHandler;
import io.github.theblacksquidward.squidwardbot.moderation.event.logging.guild.GuildMemberUpdateEventHandler;
import net.dv8tion.jda.api.JDABuilder;

@SquidwardBotModule
public class ModerationModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_moderation";
    }

    @Override
    public void registerCommands(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand(new DeafeanCommand());
        commandRegistry.registerCommand(new UndeafenCommand());
        commandRegistry.registerCommand(new UnnickCommand());
    }

    @Override
    public void onJDABuild(JDABuilder jdaBuilder) {
        jdaBuilder.addEventListeners(
                new RoleUpdateEventHandler(),
                new GlobalMemberUpdateEventHandler(),
                new VoiceChannelUpdateEventHandler(),
                new MessageUpdateEventHandler(),
                new GlobalChannelUpdateEventHandler(),
                new GuildChannelUpdateEventHandler(),
                new GuildMemberUpdateEventHandler(),
                new WelcomeMessageEventHandler()
        );
    }

}
