package io.github.theblacksquidward.squidwardbot.core.events;

import com.mongodb.client.MongoCollection;
import io.github.theblacksquidward.squidwardbot.core.SquidwardBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import static com.mongodb.client.model.Filters.eq;

public class GuildDBEvent extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        final Guild guild = event.getGuild();
        final MongoCollection<Document> guildCollection = SquidwardBot.getInstance().getMongoDatabase().getCollection("guilds");
        if (guildCollection.find(eq("guild_id", guild.getIdLong())).first() == null) {
            guildCollection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("guild_id", guild.getIdLong()));
        }
    }
}
