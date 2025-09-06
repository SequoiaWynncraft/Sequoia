package star.sequoia2.client.types.ws.handler.ws;

import com.wynntils.utils.mc.McUtils;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SCommandResultWSMessage;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SCommandResultWSMessageHandler extends WSMessageHandler implements TeXParserAccessor {
    public SCommandResultWSMessageHandler(String message) {
        super(GSON.fromJson(message, SCommandResultWSMessage.class), message);
    }

    @Override
    public void handle() {
        SCommandResultWSMessage msg = (SCommandResultWSMessage) wsMessage;
        SCommandResultWSMessage.Data data = msg.getChatMessage();
        if (data == null) return;

        boolean isError = data.error();
        String result = data.result() == null ? "" : data.result();

        McUtils.sendMessageToClient(SeqClient.prefix(teXParser().parseMutableText(result)));
    }
}
