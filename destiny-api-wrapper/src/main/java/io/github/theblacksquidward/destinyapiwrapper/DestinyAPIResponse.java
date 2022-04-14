package io.github.theblacksquidward.destinyapiwrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record DestinyAPIResponse(JsonObject jsonObject) {

    //TODO document

    public JsonObject getFullResponse() {
        return jsonObject;
    }

    public JsonElement getResponse() {
        return getFullResponse().get("Response");
    }
    public JsonElement getErrorCode() {
        return getFullResponse().get("ErrorCode");
    }
    public JsonElement getThrottleSeconds() {
        return getFullResponse().get("ThrottleSeconds");
    }
    public JsonElement getErrorStatus() {
        return getFullResponse().get("ErrorStatus");
    }
    public JsonElement getMessage() {
        return getFullResponse().get("Message");
    }
    public JsonElement getMessageData() {
        return getFullResponse().get("MessageData");
    }

    @Override
    public String toString() {
        return "DestinyAPIResponse{" +
                ", response=" + getResponse() +
                ", errorCode=" + getErrorCode() +
                ", throttleSeconds=" + getThrottleSeconds() +
                ", errorStatus=" + getErrorStatus() +
                ", message=" + getMessage() +
                ", messageData=" + getMessageData() +
                '}';
    }

}
