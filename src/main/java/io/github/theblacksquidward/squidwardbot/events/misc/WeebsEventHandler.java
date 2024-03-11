package io.github.theblacksquidward.squidwardbot.events.misc;

import io.github.theblacksquidward.squidwardbot.Environment;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

/**
 * Custom Event Handler for the Weebs server.
 */
@EventListener
public class WeebsEventHandler extends SquidwardBotEventListener {

    private static final Long WEEBS_SERVER_KAIOKEN_X4_ID = 925177162452066354L;
    private static final Long WEEBS_SERVER_BASE_ID = 925176946197938266L;

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        Guild guild = event.getGuild();
        if (guild.getIdLong() != Environment.getInstance().getWeebsServerId()) return;
        if (event.getRoles().contains(guild.getRoleById(WEEBS_SERVER_KAIOKEN_X4_ID))) {
            guild.removeRoleFromMember(event.getUser(), guild.getRoleById(WEEBS_SERVER_BASE_ID)).queue();
        }
    }

}
