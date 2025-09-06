package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.annotations.SerializedName;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SIC3DataWSMessage extends WSMessage {
    public SIC3DataWSMessage(Data data) {
        super(WSMessageType.G_IC3H.getValue(), GSON.toJsonTree(data));
    }

    public Data getChatMessage() {
        return GSON.fromJson(getData(), Data.class);

    }

    public record Data(
            @SerializedName("op_code") int opCode,
            int sequence,
            String method,
            int[] payload,
            String origin) {
    }
}
