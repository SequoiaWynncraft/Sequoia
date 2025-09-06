package star.sequoia2.client.types.ws.handler;


import star.sequoia2.client.types.ws.message.WSMessage;

public abstract class WSMessageHandler {
    protected final WSMessage wsMessage;
    protected String message;

    protected WSMessageHandler(WSMessage wsMessage, String message) {
        this.wsMessage = wsMessage;
        this.message = message;
    }

    public abstract void handle();
}