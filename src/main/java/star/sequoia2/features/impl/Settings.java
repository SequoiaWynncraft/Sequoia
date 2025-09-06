package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import star.sequoia2.client.SeqClient;
import star.sequoia2.events.input.KeyEvent;
import star.sequoia2.gui.Fonts;
import star.sequoia2.gui.screen.ClickGUIScreen;
import star.sequoia2.features.Feature;
import star.sequoia2.settings.Binding;
import star.sequoia2.settings.types.*;
import star.sequoia2.utils.render.Themes;
import lombok.Getter;
import mil.nga.color.Color;
import org.lwjgl.glfw.GLFW;

import static star.sequoia2.client.SeqClient.mc;


public class Settings extends Feature {

    public final KeybindSetting menuKeybind = settings().binding("GuiKey:", "Opens the ClickGui", Binding.withKey(GLFW.GLFW_KEY_O));

    @Getter
    public EnumSetting<Themes.ThemeEnum> theme = settings().options("Theme", "Theme Setting", Themes.ThemeEnum.NEXUS, Themes.ThemeEnum.class);

    public CalculatedEnumSetting<Fonts.Font> defaultFont = settings().options("Font", "HUD font", "Arial", () -> SeqClient.getFonts().fonts());

    IntSetting volume = settings().number("Volume", "Volume of UI sounds.", 100, 0, 100);

    @Getter
    public ClickGUIScreen clickGui;

    public Settings() {
        super("Settings", "Settings settings");
    }

    public int getSoundVolume() {
        return volume.get();
    }

    public int getPrimaryColor() {
        return theme.get().getTheme().NORMAL;
    }

    public int getSecondaryColor() {
        return theme.get().getTheme().DARK;
    }

    @Subscribe
    public void onKeyDown(KeyEvent event) {
        if (event.isKeyDown() && this.menuKeybind.get().matches(event) && mc.currentScreen == null) {
            event.cancel();
            clickGui = new ClickGUIScreen();
            mc.setScreen(clickGui);
        }
    }

    public Color colorWithAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static float[] convertToHSB(ColorSetting color) {
        Color value = color.get();
        float[] hsbVals = java.awt.Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
        return new float[] { hsbVals[0], hsbVals[1], hsbVals[2],  value.getAlpha() / 255.0f };
    }
}
