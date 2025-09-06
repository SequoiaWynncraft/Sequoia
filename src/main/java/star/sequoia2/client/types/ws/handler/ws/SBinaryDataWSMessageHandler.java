package star.sequoia2.client.types.ws.handler.ws;


import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SBinaryDataWSMessage;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SBinaryDataWSMessageHandler extends WSMessageHandler {
    public SBinaryDataWSMessageHandler(String message) {
        super(GSON.fromJson(message, SBinaryDataWSMessage.class), message);
    }

    @Override
    public void handle() {
        SBinaryDataWSMessage sBinaryDataWSMessage = GSON.fromJson(message, SBinaryDataWSMessage.class);
        SBinaryDataWSMessage.Data sBinaryDataWSMessageData = sBinaryDataWSMessage.getSBinaryData();

        SeqClient.debug("Received SBinaryData: " + sBinaryDataWSMessageData);
    }
}