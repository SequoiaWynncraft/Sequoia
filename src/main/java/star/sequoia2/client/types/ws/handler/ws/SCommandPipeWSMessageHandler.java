package star.sequoia2.client.types.ws.handler.ws;

import org.apache.commons.lang3.StringUtils;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SCommandPipeWSMessage;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SCommandPipeWSMessageHandler extends WSMessageHandler {
    public SCommandPipeWSMessageHandler(String message) {
        super(GSON.fromJson(message, SCommandPipeWSMessage.class), message);
    }

    @Override
    public void handle() {
        if (StringUtils.equals("Invalid token", wsMessage.getData().getAsString())) {
            SeqClient.debug("Received invalid token response. Requesting a new token.");
            SeqClient.getWebSocketFeature().authenticate(true);
        } else if (StringUtils.equals("Authenticated.", wsMessage.getData().getAsString())) {
            SeqClient.debug("Authenticated with WebSocket server.");
            SeqClient.getWebSocketFeature().setAuthenticating(false);
            SeqClient.getWebSocketFeature().setAuthenticated(true);
        }
    }
}