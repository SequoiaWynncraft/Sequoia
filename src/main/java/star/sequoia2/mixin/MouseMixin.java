package star.sequoia2.mixin;

import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.events.input.MouseButtonEvent;
import star.sequoia2.events.input.MouseCursorPosEvent;
import star.sequoia2.events.input.MouseScrollEvent;
import net.minecraft.client.Mouse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin implements EventBusAccessor {
    @Inject(method = "onMouseButton", at = @At(value = "HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        dispatch(new MouseButtonEvent(window, button, action, mods), o -> ci.cancel());
    }

    @Inject(method = "onCursorPos", at = @At(value = "HEAD"), cancellable = true)
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        dispatch(new MouseCursorPosEvent(window, x, y), o -> ci.cancel());
    }

    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        dispatch(new MouseScrollEvent(window, horizontal, vertical), o -> ci.cancel());
    }
}
