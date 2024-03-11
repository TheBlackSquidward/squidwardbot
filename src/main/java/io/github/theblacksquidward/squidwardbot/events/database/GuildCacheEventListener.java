package io.github.theblacksquidward.squidwardbot.events.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.github.theblacksquidward.squidwardbot.core.events.EventListener;
import io.github.theblacksquidward.squidwardbot.core.events.SquidwardBotEventListener;
import io.github.theblacksquidward.squidwardbot.database.SquidwardBotDatabase;
import io.github.theblacksquidward.squidwardbot.database.models.GuildModel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

@EventListener
public class GuildCacheEventListener extends SquidwardBotEventListener {

  @Override
  public void onGuildJoin(@NotNull GuildJoinEvent event) {
    final Guild guild = event.getGuild();
    final MongoCollection<GuildModel> guildCollection =
        SquidwardBotDatabase.getInstance().getGuildCollection();
    if (guildCollection.find(Filters.eq("guild_id", guild.getIdLong())).first() == null) {
      guildCollection.insertOne(
          new GuildModel(new ObjectId(), guild.getIdLong(), null, null, null, null, null));
    }
  }
}
