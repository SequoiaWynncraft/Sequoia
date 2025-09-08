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
    public EnumSetting<Themes.ThemeEnum> theme = settings().options("ChatTheme", "ChatTheme Setting", Themes.ThemeEnum.NEXUS, Themes.ThemeEnum.class);

    public CalculatedEnumSetting<Fonts.Font> defaultFont = settings().options("Font", "HUD font", "Arial", () -> SeqClient.getFonts().fonts());

    IntSetting volume = settings().number("Volume", "Volume of UI sounds.", 100, 0, 100);

    ColorSetting colorNormal = settings().color("Normal", "Normal color", new Color(39473836));
    ColorSetting colorDark = settings().color("Dark", "Dark color", new Color(-14012845));
    ColorSetting colorLight = settings().color("Light", "Light color", new Color(-66308));
    ColorSetting colorAccent1 = settings().color("Accent1", "Accent1 color", new Color(-12041351));
    ColorSetting colorAccent2 = settings().color("Accent2", "Accent2 color", new Color(-12615215));
    ColorSetting colorAccent3 = settings().color("Accent3", "Accent3 color", new Color(-12615215));

    @Getter
    public ClickGUIScreen clickGui;

    public Settings() {
        super("Settings", "Settings settings");
    }

    public int getSoundVolume() {
        return volume.get();
    }

    public int getNormalColorInt() {
        return theme.get().getTheme().NORMAL;
    }

    public Color getThemeNormal() {
        return colorNormal.get();
    }

    public Color getThemeDark() {
        return colorDark.get();
    }

    public Color getThemeLight() {
        return colorLight.get();
    }

    public Color getThemeAccent1() {
        return colorAccent1.get();
    }

    public Color getThemeAccent2() {
        return colorAccent2.get();
    }

    public Color getThemeAccent3() {
        return colorAccent3.get();
    }

    @Subscribe
    public void onKeyDown(KeyEvent event) {
        if (event.isKeyDown() && this.menuKeybind.get().matches(event) && mc.currentScreen == null) {
            event.cancel();
            clickGui = new ClickGUIScreen();
            mc.setScreen(clickGui);
        }
    }

    public static float[] convertToHSB(ColorSetting color) {
        Color value = color.get();
        float[] hsbVals = java.awt.Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
        return new float[] { hsbVals[0], hsbVals[1], hsbVals[2],  value.getAlpha() / 255.0f };
    }
}
