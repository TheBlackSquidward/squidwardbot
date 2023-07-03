package io.github.theblacksquidward.squidwardbot.core.models;

import org.bson.types.ObjectId;

public record GuildModel(
        ObjectId _id,
        Long guild_id,
        Long member_update_logging_channel_id,
        Long vc_update_logging_channel_id,
        Long channel_update_logging_channel_id,
        Long role_update_logging_channel_id,
        Long command_update_logging_channel_id
) {
}
