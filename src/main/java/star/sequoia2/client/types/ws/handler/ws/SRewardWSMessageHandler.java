package star.sequoia2.client.types.ws.handler.ws;

import com.google.gson.JsonElement;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SRewardWSMessage;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SRewardWSMessageHandler extends WSMessageHandler {
    public SRewardWSMessageHandler(String message) {
        super(GSON.fromJson(message, SRewardWSMessage.class), message);
    }
    @Override
    public void handle() {
        SRewardWSMessage sRewardWSMessage = (SRewardWSMessage) wsMessage;
        JsonElement sRewardWSMessageData = sRewardWSMessage.getData();


    }
}
