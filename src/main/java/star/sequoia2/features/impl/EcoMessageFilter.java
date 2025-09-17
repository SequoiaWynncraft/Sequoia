package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import star.sequoia2.events.PacketEvent;
import star.sequoia2.features.ToggleFeature;

import static star.sequoia2.client.SeqClient.mc;

public class EcoMessageFilter extends ToggleFeature {
    // random comment
    public EcoMessageFilter() {
        super("EcoMessageFilter", "Filters eco messages in chat for Strat+");
    }

    @Subscribe
    public void cancelPackets(PacketEvent.PacketReceiveEvent event){
        if (mc.player != null && event.packet() instanceof GameMessageS2CPacket packet){
            if (!packet.overlay()){
                if (containsEcoMessage(packet.content())){
                    event.cancel();
                }
            }
        }
    }
    public boolean containsEcoMessage(Text message){
        if (message.toString().contains("[literal{\uDAFF\uDFFC\uE001\uDB00\uDC06}[style={font=minecraft:chat/prefix}]")
                || message.toString().contains("[literal{\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE}[style={font=minecraft:chat/prefix}]")){
            if (message.toString().contains("literal{ removed }") && (message.toString().contains("literal{ from }")) || message.toString().contains("literal{from }")){return true;}
            if (message.toString().contains("literal{ set }") && message.toString().contains("literal{ to level }") && message.toString().contains("literal{ on }")){return true;}
            if (message.toString().contains("literal{ changed }") && (message.toString().contains("bonuses}[style={color=yellow}]") || message.toString().contains("upgrades}[style={color=yellow}]")) && message.toString().contains("literal{ on }")){return true;}
            if (message.toString().contains("literal{ changed the") && (message.toString().contains("literal{cheapest}") || message.toString().contains("literal{fastest}"))){return true;}
            if ((message.toString().contains("literal{ changed the") || message.toString().contains("tax") )){return true;}
            if (message.toString().contains("literal{ changed the }") && message.toString().contains("literal{borders") && (message.toString().contains("to close") || message.toString().contains("to open"))){return true;}
            if (message.toString().contains("literal{ applied the loadout }") || message.toString().contains("literal{ updated Loadout }")){return true;}
            if (message.toString().contains("literal{ deleted the }") && message.toString().contains("literal{ loadout}")){return true;}
            if (message.toString().contains("literal{ Territory }")
                    && (message.toString().contains("literal{ is producing more")
                    || message.toString().contains("literal{ production has stabilised}") || message.toString().contains("literal{ is using more"))){return true;}
        }
        return false;
    }

}
