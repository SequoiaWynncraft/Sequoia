package star.sequoia2.client.types.ws.handler.ws;

import org.apache.commons.lang3.StringUtils;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SSessionResultWSMessage;

import java.util.regex.Pattern;

import static star.sequoia2.client.commands.DisconnectCommand.deleteToken;
import static star.sequoia2.client.types.ws.WSConstants.GSON;


public class SSessionResultWSMessageHandler extends WSMessageHandler {
    private static final Pattern JWT_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");

    public SSessionResultWSMessageHandler(String message) {
        super(GSON.fromJson(message, SSessionResultWSMessage.class), message);
    }

    @Override
    public void handle() {
        SSessionResultWSMessage sSessionResultWSMessage = GSON.fromJson(message, SSessionResultWSMessage.class);
        SSessionResultWSMessage.Data sSessionResultWSMessageData = sSessionResultWSMessage.getSSessionResultData();

        if (sSessionResultWSMessageData.error()) {
            return;
        }

        String result = sSessionResultWSMessageData.result();

//        Sequoia2.debug("SessionResult:" + result);

        if (StringUtils.equals(result, "Invalid token")) {
            Sequoia2.debug("Invalid token.");
            deleteToken();
            return;
        }

        if (StringUtils.equals(result, "Authentication pending.")) {
            Sequoia2.getWebSocketFeature().setAuthenticating(true);
            Sequoia2.getWebSocketFeature().setAuthenticated(false);
            Sequoia2.debug("Authentication pending, waiting for successful authentication.");
            return;
        }

        if (StringUtils.equals(result, "Authenticated.")) {
            Sequoia2.getWebSocketFeature().setAuthenticating(false);
            Sequoia2.getWebSocketFeature().setAuthenticated(true);
            Sequoia2.debug("Websocket authenticated!");
            return;
        }

        if (StringUtils.equals(result, "REDACTED")) {
            Sequoia2.debug("Token redacted.");
            return;
        }

        if (JWT_PATTERN.matcher(result).matches()) {

            Sequoia2.getWebSocketFeature().setAuthenticating(false);
            Sequoia2.getWebSocketFeature().setAuthenticated(true);
            Sequoia2.debug("Authenticated with WebSocket server.");

            if (!StringUtils.equals(AccessTokenManager.retrieveAccessToken(), result)) {
                AccessTokenManager.storeAccessToken(result);
            }
        } else {
            Sequoia2.error("Failed to authenticate with WebSocket server: " + result);
        }
    }
}