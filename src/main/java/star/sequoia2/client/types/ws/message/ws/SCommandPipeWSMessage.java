package star.sequoia2.client.types.ws.message.ws;


import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SCommandPipeWSMessage extends WSMessage {
    public SCommandPipeWSMessage(String data) {
        super(WSMessageType.S_COMMAND_PIPE.getValue(), GSON.toJsonTree(data));
    }
}