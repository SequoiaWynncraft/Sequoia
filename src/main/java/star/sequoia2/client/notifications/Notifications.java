package star.sequoia2.client.notifications;

import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.client.NectarClient;
import star.sequoia2.client.types.IChatHud;
import star.sequoia2.features.impl.Client;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static star.sequoia2.client.NectarClient.mc;

public class Notifications implements FeaturesAccessor {

    private Text lastMessage;

    public void sendAlert(Text alert) {
        if (notReady()) return;
        mc.inGameHud.setTitle(alert);
    }

    public void sendNotification(Text notification) {
        if (notReady()) return;
        mc.inGameHud.setOverlayMessage(notification, false);
    }

    public void sendMessage(Text message) {
        sendMessage(message, String.valueOf(message));
    }

    /**
     *
     * @param message message to send
     * @param sig to detect to override a duplicate message
     * @READNIGGA NEVER USE THIS INSIDE A MESSAGE EVENT SUBSCRIBER, WILL RESULT IN STACKOVERFLOW.
     */
    public void sendMessage(Text message, String sig) {
        if (notReady()) return;

        int color = feature(Client.class).getPrimaryColor();
        MutableText prefix = Text.literal("[Nectar] ").withColor(color);
        if (message.getStyle().getColor() == null) {
            prefix.append(MutableText.of(message.getContent()).formatted(Formatting.WHITE));
        } else {
            prefix.append(message);
        }
        var signature = prefix.toString() != null ? new MessageSignatureData(createSignature(Text.literal("[Nectar] ").withColor(color).toString() + sig)) : null;

//        ((IChatHud) mc.inGameHud.getChatHud()).nectar$remove(signature);
        ((IChatHud) mc.inGameHud.getChatHud()).nectar$invokeAddMessage(prefix, signature, MessageIndicator.system());
    }


    public void sendMultilineMessage(List<Text> messages) {
        if (messages.isEmpty()) {
            return;
        }
        sendMessage(messages.getFirst(), messages.getFirst().toString());
        for (int i = 1; i < messages.size(); i++) {
            mc.inGameHud.getChatHud().addMessage(messages.get(i));
        }
    }

    private static byte[] createSignature(String identifier) {
        byte[] bytes = new byte[256];
        byte[] identifierBytes = identifier.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(identifierBytes, 0, bytes, 0, Math.min(bytes.length, identifierBytes.length));
        return bytes;
    }

    private static boolean notReady() {
        return mc.inGameHud == null || !NectarClient.initialized;
    }
}
