package star.sequoia2.mixin;

import com.google.common.collect.Lists;
import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.client.types.IChatHud;
import star.sequoia2.events.ChatMessageEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.ListIterator;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud, EventBusAccessor {
    @Shadow
    protected abstract void refresh();

    @Shadow
    private final List<ChatHudLine> messages = Lists.newArrayList();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        dispatch(new ChatMessageEvent(message, signatureData, indicator), o -> ci.cancel());
    }

    @Override
    @Invoker("addMessage")
    public abstract void nectar$invokeAddMessage(Text text, @Nullable MessageSignatureData sig, @Nullable MessageIndicator indicator);

    @Override
    public void nectar$remove(@Nullable MessageSignatureData signature) {
        if (signature == null)
            return;

        ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
        boolean changed = false;
        while (listIterator.hasNext()) {
            ChatHudLine message = listIterator.next();
            if (signature.equals(message.signature())) {
                listIterator.remove();
                changed = true;
            }
        }

        if (changed) {
            this.refresh();
        }
    }
}
