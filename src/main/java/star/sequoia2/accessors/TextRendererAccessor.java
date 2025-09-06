package star.sequoia2.accessors;

import star.sequoia2.client.SeqClient;
import star.sequoia2.features.impl.Settings;
import net.minecraft.client.font.TextRenderer;

import static star.sequoia2.client.SeqClient.mc;

public interface TextRendererAccessor {
    default TextRenderer textRenderer() {
        return SeqClient.getFeatures().get(Settings.class)
                .map(hud -> hud.defaultFont.getOption().renderer())
                .orElse(mc.textRenderer);
    }
}
