package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import lombok.Getter;
import star.sequoia2.events.Render2DEvent;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.gui.screen.CustomChatScreen;

import static star.sequoia2.client.SeqClient.mc;

public class CustomChat extends ToggleFeature {
    @Getter
    private CustomChatScreen screen;

    public CustomChat() {
        super("CustomChat", "CustomChat toggle");
    }

    @Override
    public void onDeactivate() {
        if (screen != null) {
            if (screen.getBrowser() != null) {
                screen.getBrowser().stopLoad();
                screen.getBrowser().close();
            }
            screen = null;
        }
    }

    @Subscribe
    public void onRender2D(Render2DEvent event) {
        if (screen == null) screen = new CustomChatScreen();
        if (mc.currentScreen == null) {
            screen.ensureReady();
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();
            screen.renderBrowser(event.context(), 0, 0, w, h);
        }
    }
}
