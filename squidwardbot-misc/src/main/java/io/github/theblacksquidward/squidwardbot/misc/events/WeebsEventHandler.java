package io.github.theblacksquidward.squidwardbot.misc.events;

import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Custom Event Handler for the Weebs server.
 */
public class WeebsEventHandler extends ListenerAdapter {

    private static final Long WEEBS_SERVER_KAIOKEN_X4_ID = 925177162452066354L;
    private static final Long WEEBS_SERVER_BASE_ID = 925176946197938266L;

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        Guild guild = event.getGuild();
        if (guild.getIdLong() != SquidwardBot.getInstance().getWeebsServerId()) return;
        if (event.getRoles().contains(guild.getRoleById(WEEBS_SERVER_KAIOKEN_X4_ID))) {
            guild.removeRoleFromMember(event.getUser(), guild.getRoleById(WEEBS_SERVER_BASE_ID)).queue();
        }
    }

}
