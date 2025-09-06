package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.annotations.SerializedName;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class GChatMessageWSMessage extends WSMessage {
    public GChatMessageWSMessage(Data data) {
        super(WSMessageType.G_CHAT_MESSAGE.getValue(), GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return GSON.fromJson(getData(), Data.class);

    }

    public record Data(
            String username,
            String nickname,
            String message,
            String timestamp,
            @SerializedName("client_name") String clientName) {}
}