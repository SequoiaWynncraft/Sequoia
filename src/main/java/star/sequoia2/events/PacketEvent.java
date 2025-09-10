package star.sequoia2.events;

import com.collarmc.pounce.Cancelable;
import net.minecraft.network.packet.Packet;

public record PacketEvent() implements Cancelable {
    public record PacketReceiveEvent(Packet<?> packet) implements Cancelable {}
    public record PacketSendEvent(Packet<?> packet) implements Cancelable {}
}
