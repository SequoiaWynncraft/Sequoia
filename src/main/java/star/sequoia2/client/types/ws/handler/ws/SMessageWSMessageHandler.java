package star.sequoia2.client.types.ws.handler.ws;

import com.google.gson.JsonElement;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SMessageWSMessage;
import star.sequoia2.features.impl.ws.WebSocketFeature;
import star.sequoia2.utils.URLUtils;

import java.util.regex.Matcher;

import static star.sequoia2.client.types.ws.WSConstants.GSON;
import static star.sequoia2.utils.XMLUtils.extractTextFromXml;


public class SMessageWSMessageHandler extends WSMessageHandler implements FeaturesAccessor, TeXParserAccessor {
    public SMessageWSMessageHandler(String message) {
        super(GSON.fromJson(message, SMessageWSMessage.class), message);
    }

    @Override
    public void handle() {
        SMessageWSMessage sMessageWSMessage = (SMessageWSMessage) wsMessage;
        JsonElement sMessageWSMessageData = sMessageWSMessage.getData();

        if (sMessageWSMessageData.isJsonPrimitive()) {
            String serverMessageText = sMessageWSMessageData.getAsString();
            if (StringUtils.equals(serverMessageText, "Invalid or expired token provided.\\nVisit https://api.sequoia.ooo/oauth2 to obtain a new session.")) {
                SeqClient.debug("Received authentication required message, reauthenticating.");
                features().getIfActive(WebSocketFeature.class).ifPresent(WebSocketFeature::authenticate);
                return;
            }

            String trimmed = serverMessageText == null ? "" : serverMessageText.trim();
            if (trimmed.startsWith("<")) {
                String tex = extractTextFromXml(serverMessageText);
                MutableText messageComponent = Text.literal("Server message ➤ ").append(teXParser().parseMutableText(tex));
                McUtils.sendMessageToClient(SeqClient.prefix(messageComponent));
                return;
            }

            Matcher matcher = URLUtils.getURLMatcher(serverMessageText);
            MutableText messageComponent = Text.literal("Server message ➤ ");
            int lastMatchEnd = 0;

            while (matcher.find()) {
                if (matcher.start() > lastMatchEnd) {
                    messageComponent = messageComponent.append(
                            Text.literal(serverMessageText.substring(lastMatchEnd, matcher.start()))
                                    .styled(style -> style.withColor(0x19A775)));
                }
//
                String url = matcher.group();
                messageComponent =
                        messageComponent.append(Text.literal(url).styled(style -> style.withColor(0x1DA1F2)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, Text.literal("Click to open URL")))));

                lastMatchEnd = matcher.end();
            }

            if (lastMatchEnd < serverMessageText.length()) {
                messageComponent = messageComponent.append(Text.literal(serverMessageText.substring(lastMatchEnd))
                        .styled(style -> style.withColor(0x19A775)));
            }

            McUtils.sendMessageToClient(SeqClient.prefix(messageComponent));
        } else {
            McUtils.sendMessageToClient(
                    SeqClient.prefix(Text.literal("Server message ➤ " + sMessageWSMessageData))
                            .styled(style -> style.withColor(0x19A775)));
        }
    }
}
