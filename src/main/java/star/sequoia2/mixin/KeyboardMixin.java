package star.sequoia2.mixin;

import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.events.input.KeyEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin implements EventBusAccessor {
    @Inject(method = "onKey", at = @At(value = "HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        dispatch(new KeyEvent(window, key, scancode, action, modifiers), e -> ci.cancel());
    }
}