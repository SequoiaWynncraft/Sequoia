package star.sequoia2.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.SeqClickEvent;
import star.sequoia2.features.impl.Client;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin implements FeaturesAccessor {
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 1, remap = false), cancellable = true)
    private void onRunCommand(Style style, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent event = style.getClickEvent();
        if (event != null && event instanceof SeqClickEvent clickEvent && clickEvent.value.startsWith(feature(Client.class).getPrefix())) {
            try {
                SeqClient.getCommands().dispatch(clickEvent.value.substring(feature(Client.class).getPrefix().length()));
                cir.setReturnValue(true);
            } catch (CommandSyntaxException e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }
}