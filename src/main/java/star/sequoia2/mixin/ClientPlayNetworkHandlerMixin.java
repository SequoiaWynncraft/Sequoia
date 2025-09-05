package star.sequoia2.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.NotificationsAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.features.impl.Client;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static star.sequoia2.client.SeqClient.mc;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 100)
public abstract class ClientPlayNetworkHandlerMixin implements EventBusAccessor, FeaturesAccessor, NotificationsAccessor {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith(feature(Client.class).getPrefix())) {
            try {
                SeqClient.getCommands().dispatch(message.substring(feature(Client.class).getPrefix().length()));
            } catch (CommandSyntaxException e) {
                notifications().sendMessage(Text.of(e.getMessage()));
            }

            mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }
}
