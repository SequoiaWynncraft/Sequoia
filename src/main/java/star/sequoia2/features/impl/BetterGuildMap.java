package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import star.sequoia2.events.input.KeyEvent;
import star.sequoia2.features.Feature;
import star.sequoia2.gui.screen.BetterGuildMapScreen;
import star.sequoia2.settings.Binding;
import star.sequoia2.settings.types.KeybindSetting;

import static star.sequoia2.client.SeqClient.mc;

public class BetterGuildMap extends Feature {

    public final KeybindSetting menuKeybind = settings().binding("GuiKey:", "Opens the Custom gui", Binding.none());

    public BetterGuildMap() {
        super("BetterGuildMap", "Replaces wynntils guild map for an improved RTS one");
    }

    @Subscribe
    public void onKeyDown(KeyEvent event) {
        if (event.isKeyDown() && this.menuKeybind.get().matches(event) && mc.currentScreen == null) {
            event.cancel();
            mc.setScreen(new BetterGuildMapScreen());
        }
    }
}
