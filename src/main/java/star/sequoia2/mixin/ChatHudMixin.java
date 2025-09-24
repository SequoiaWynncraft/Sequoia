package star.sequoia2.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.events.ChatMessageEvent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements EventBusAccessor {

    @Unique
    private static final int   CACHE_SIZE     = 256;    // cap memory
    @Unique
    private static final long  DUP_WINDOW_MS  = 1000L;  // treat identical messages within 1s as dupes

    @Unique
    private static final Map<Integer, Long> SEQ$RECENT =
            Collections.synchronizedMap(new LinkedHashMap<Integer, Long>(CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Long> eldest) {
                    return size() > CACHE_SIZE;
                }
            });

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("TAIL"))
    private void onChatMessageEnd(Text message, CallbackInfo ci) {
        dispatch(new ChatMessageEvent(message));
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void sequoia$dedupeHead(Text message, CallbackInfo ci) {
        if (seq$shouldDrop(message)) {
            ci.cancel();
        }
    }

    @Unique
    private static int seq$keyFrom(Text msg) {
        String s = msg.getString().replaceAll("\\s+", " ").trim();
        return s.hashCode();
    }

    @Unique
    private static boolean seq$shouldDrop(Text msg) {
        long now = System.currentTimeMillis();
        int key = seq$keyFrom(msg);
        synchronized (SEQ$RECENT) {
            Long last = SEQ$RECENT.get(key);
            if (last != null && (now - last) <= DUP_WINDOW_MS) {
                return true;
            }
            SEQ$RECENT.put(key, now);
        }
        return false;
    }
}
