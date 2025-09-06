package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.JsonElement;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

public class SMessageWSMessage extends WSMessage {
    public SMessageWSMessage(JsonElement data) {
        super(WSMessageType.S_MESSAGE.getValue(), data);
    }
}