package star.sequoia2.client.types.ws.message.ws;


import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class GClientCommandWSMessage extends WSMessage {
    public GClientCommandWSMessage(Data data) {
        super(WSMessageType.G_CLIENT_COMMAND.getValue(), GSON.toJsonTree(data));
    }

    public Data getClientCommandData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            String command,
            String args
    ) {}

}
