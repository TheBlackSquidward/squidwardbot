package io.github.theblacksquidward.squidwardbot.core.models;

import org.bson.types.ObjectId;

public record MessageModel(
        ObjectId _id,
        Long guild_id,
        Long channel_id,
        Long author_id,
        Long message_id,
        String content,
        String raw_content
) {
}
