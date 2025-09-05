package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import star.sequoia2.client.SeqClient;
import star.sequoia2.events.input.KeyEvent;
import star.sequoia2.gui.Fonts;
import star.sequoia2.gui.screen.ClickGUIScreen;
import star.sequoia2.features.Feature;
import star.sequoia2.settings.Binding;
import star.sequoia2.settings.types.*;
import star.sequoia2.utils.Themes;
import lombok.Getter;
import mil.nga.color.Color;
import org.lwjgl.glfw.GLFW;

import static star.sequoia2.client.SeqClient.mc;


public class Client extends Feature {

    public final KeybindSetting menuKeybind = settings().binding("GuiKey:", "Opens the ClickGui", Binding.withKey(GLFW.GLFW_KEY_O));

    private final TextSetting prefix = settings().text("Command Prefix", "Prefix for commands", ":");

    private final BooleanSetting plus = settings().bool("Plus", "The plus thing in the features", true);

    private final ColorSetting primary = settings().color("Primary", "The primary color", Color.color(-1838396983));
    private final ColorSetting secondary = settings().color("Secondary", "The primary color", Color.color(-1838396983));

    private final ColorSetting buttonPrimary = settings().color("Button", "The primary color", Color.color(-1838396983));

    private final ColorSetting backgroundPrimary = settings().color("Background", "The primary color", Color.color(-1838396983));

    private final ColorSetting textPrimaryColor = settings().color("Text primary", "The primary text color", Color.white());
    private final ColorSetting textSecondaryColor = settings().color("Text secondary", "The secondary text color", Color.white());

    public EnumSetting<Themes.Theme> themeControlEnumSetting = settings().options("Theme", "Theme Setting", Themes.Theme.NOTHING, Themes.Theme.class);

    public CalculatedEnumSetting<Fonts.Font> defaultFont = settings().options("Font", "HUD font", "Arial", () -> SeqClient.getFonts().fonts());

    IntSetting volume = settings().number("Volume", "Volume of UI sounds.", 100, 0, 100);

    @Getter
    public ClickGUIScreen clickGui;

    public Client() {
        super("Client", "Client settings");
    }

    public int getSoundVolume() {
        return volume.get();
    }

    public int getButtonPrimary() {
        return buttonPrimary.get().getColorWithAlpha();
    }

    public int getBackgroundPrimary() {
        return backgroundPrimary.get().getColorWithAlpha();
    }

    public int getBackgroundDarker(int darkness) {
        darkness = Math.max(0, Math.min(darkness, 255));

        int color = backgroundPrimary.get().getColorWithAlpha();

        int alpha = (color >> 24) & 0xff;
        int red   = (color >> 16) & 0xff;
        int green = (color >> 8)  & 0xff;
        int blue  = (color)       & 0xff;

        float factor = 1.0f - (darkness / 255.0f);

        red   = (int)(red * factor);
        green = (int)(green * factor);
        blue  = (int)(blue * factor);

        red   = Math.max(0, Math.min(red, 255));
        green = Math.max(0, Math.min(green, 255));
        blue  = Math.max(0, Math.min(blue, 255));

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }


    public int getPrimaryColor() {
        return primary.get().getColorWithAlpha();
    }

    public int getSecondaryColor() {
        return secondary.get().getColorWithAlpha();
    }

    public int getTextPrimaryColor() {
        return textPrimaryColor.get().getColorWithAlpha();
    }

    public int getTextSecondaryColor() {
        return textSecondaryColor.get().getColorWithAlpha();
    }

    public int getTextPrimaryColorWithAlpha(float alpha) {
        return colorWithAlpha(textPrimaryColor.get(), alpha).getColorWithAlpha();
    }

    public int getTextSecondaryColorWithAlpha(float alpha) {
        return colorWithAlpha(textSecondaryColor.get(), alpha).getColorWithAlpha();
    }

    public boolean getPlus() {
        return plus.get();
    }

    public String getPrefix() {
        return prefix.get();
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
