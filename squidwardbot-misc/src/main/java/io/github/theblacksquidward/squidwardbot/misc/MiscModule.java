package io.github.theblacksquidward.squidwardbot.misc;

import io.github.theblacksquidward.squidwardbot.core.modules.ISquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.core.modules.SquidwardBotModule;
import io.github.theblacksquidward.squidwardbot.misc.events.WeebsEventHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@SquidwardBotModule
public class MiscModule implements ISquidwardBotModule {

    @Override
    public String getModuleIdentifier() {
        return "squidwardbot_misc";
    }

    @Override
    public void onJDABuild(JDABuilder jdaBuilder) {
        jdaBuilder.enableCache(CacheFlag.ONLINE_STATUS);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.addEventListeners(new WeebsEventHandler());
    }

}
