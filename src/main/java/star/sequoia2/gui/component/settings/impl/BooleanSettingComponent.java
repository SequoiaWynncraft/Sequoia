package star.sequoia2.gui.component.settings.impl;

import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.component.settings.SettingComponent;
import star.sequoia2.settings.Setting;


public class BooleanSettingComponent extends SettingComponent<Boolean> implements RenderUtilAccessor, TextRendererAccessor {

    public BooleanSettingComponent(Setting<Boolean> setting) {
        super(setting);
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();

        float horizontalPos = left + textRenderer().getWidth(setting.name) + getGuiRoot().pad;
        float verticalPos = top + (textRenderer().fontHeight / 4f);

        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());
        Color accent2 = features().get(Settings.class).map(Settings::getThemeAccent2).orElse(Color.black());

        boolean on = Boolean.TRUE.equals(setting.get());
        Color start = on ? accent2 : dark;

        render2DUtil().roundRectFilled(context.getMatrices(), horizontalPos, verticalPos, horizontalPos + 5f, verticalPos + 5f, 2.5f, start);

        renderText(context, setting.name, left, top, light.getColor());
    }


    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (isWithin(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            boolean val = setting.get();
            setting.set(!val);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            leftShiftHeld = true;
        }
        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            leftShiftHeld = false;
        }
        super.keyReleased(keyCode, scanCode, modifiers);
    }
}
