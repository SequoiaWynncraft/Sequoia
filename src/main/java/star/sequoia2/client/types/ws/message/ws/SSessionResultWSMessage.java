package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.annotations.SerializedName;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import java.time.OffsetDateTime;

import static star.sequoia2.client.types.ws.WSConstants.GSON;


public class SSessionResultWSMessage extends WSMessage {
    public SSessionResultWSMessage(Data data) {
        super(WSMessageType.S_SESSION_RESULT.getValue(), GSON.toJsonTree(data));
    }

//    public Data getSSessionResultData() {
//
//        return GSON.fromJson(getData(), Data.class);
//
//    }

    public Data getSSessionResultData() {
        var el = getData(); // assuming this returns a JsonElement

        if (el == null || el.isJsonNull()) {
            return new Data(true, "null data", null);
        }

        if (el.isJsonObject()) {
            // normal case: {"error": false, "result": "...", "expire_at": "..."}
            return GSON.fromJson(el, Data.class);
        }

        if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
            // server sent: {"type":22, "data":"Invalid token"} OR "Authenticated." etc.
            String s = el.getAsString();
            boolean isError = "Invalid token".equals(s);
            return new Data(isError, s, null);
        }

        // fallback: unexpected structure
        return new Data(true, el.toString(), null);
    }


    public record Data(boolean error, String result, @SerializedName("expire_at") OffsetDateTime expireAt) {}
}