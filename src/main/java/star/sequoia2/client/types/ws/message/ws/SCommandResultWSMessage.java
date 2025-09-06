package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.JsonElement;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SCommandResultWSMessage extends WSMessage {
    public SCommandResultWSMessage(int type, JsonElement data) {
        super(WSMessageType.S_COMMAND_RESULT.getValue(), GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return GSON.fromJson(getData(), Data.class);

    }

    public record Data(
            boolean error,
            String result) {
    }
}
