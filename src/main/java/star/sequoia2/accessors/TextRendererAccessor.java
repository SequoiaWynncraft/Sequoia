package star.sequoia2.accessors;

import star.sequoia2.client.NectarClient;
import star.sequoia2.features.impl.Client;
import net.minecraft.client.font.TextRenderer;

import static star.sequoia2.client.NectarClient.mc;

public interface TextRendererAccessor {
    default TextRenderer textRenderer() {
        return NectarClient.getFeatures().get(Client.class)
                .map(hud -> hud.defaultFont.getOption().renderer())
                .orElse(mc.textRenderer);
    }
}
