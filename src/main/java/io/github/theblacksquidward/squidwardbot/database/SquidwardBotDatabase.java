package io.github.theblacksquidward.squidwardbot.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.theblacksquidward.squidwardbot.Environment;
import io.github.theblacksquidward.squidwardbot.core.ShutdownHooks;
import io.github.theblacksquidward.squidwardbot.database.models.GuildModel;
import io.github.theblacksquidward.squidwardbot.database.models.MessageModel;
import io.github.theblacksquidward.squidwardbot.database.models.UserModel;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SquidwardBotDatabase {

  private static final Logger LOGGER = LoggerFactory.getLogger(SquidwardBotDatabase.class);

  private static SquidwardBotDatabase INSTANCE = null;

  private final MongoCollection<GuildModel> guildCollection;
  private final MongoCollection<MessageModel> messageCollection;
  private final MongoCollection<UserModel> userCollection;

  private SquidwardBotDatabase() {
    LOGGER.info("Initializing database...");

    final CodecRegistry pojoRegistry =
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
    final CodecRegistry codecRegistry =
        CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoRegistry);

    final MongoClient mongoClient = connect(codecRegistry);
    ShutdownHooks.register(mongoClient::close);
    final MongoDatabase mongoDatabase = mongoClient.getDatabase("squidwardbot");

    this.guildCollection = mongoDatabase.getCollection("guilds", GuildModel.class);
    this.userCollection = mongoDatabase.getCollection("users", UserModel.class);
    this.messageCollection = mongoDatabase.getCollection("messages", MessageModel.class);

    LOGGER.info("Database initialized.");
  }

  public static SquidwardBotDatabase getInstance() {
    if (INSTANCE == null)
      throw new NullPointerException(
          "You cannot access the database without initializing it first!");
    return INSTANCE;
  }

  public static void init() {
    if (INSTANCE != null) throw new IllegalStateException("Database already initialized!");
    INSTANCE = new SquidwardBotDatabase();
  }

  private MongoClient connect(CodecRegistry codecRegistry) {
    final ConnectionString connectionString =
        new ConnectionString(
            "mongodb+srv://"
                + Environment.getInstance().getMongoDBUsername()
                + ":"
                + Environment.getInstance().getMongoDBPassword()
                + "@squidwardbot.nd0ncel.mongodb.net/?retryWrites=true&w=majority");
    final MongoClientSettings mongoClientSettings =
        MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applicationName("SquidwardBot")
            .codecRegistry(codecRegistry)
            .build();
    return MongoClients.create(mongoClientSettings);
  }

  public MongoCollection<GuildModel> getGuildCollection() {
    return guildCollection;
  }

  public MongoCollection<UserModel> getUserCollection() {
    return userCollection;
  }

  public MongoCollection<MessageModel> getMessageCollection() {
    return messageCollection;
  }
}
