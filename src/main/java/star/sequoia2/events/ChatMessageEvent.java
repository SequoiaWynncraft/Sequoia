package star.sequoia2.events;

import com.collarmc.pounce.Cancelable;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

public record ChatMessageEvent(Text message, MessageSignatureData signature, MessageIndicator indicator) implements Cancelable {
}
