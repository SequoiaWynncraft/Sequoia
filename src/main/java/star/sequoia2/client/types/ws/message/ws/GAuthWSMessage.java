package star.sequoia2.client.types.ws.message.ws;

import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class GAuthWSMessage extends WSMessage {
    public GAuthWSMessage(String data) {
        super(WSMessageType.G_AUTH.getValue(), GSON.toJsonTree(data));
    }

    public String getCode() {
        return GSON.fromJson(getData(), String.class);
    }
}