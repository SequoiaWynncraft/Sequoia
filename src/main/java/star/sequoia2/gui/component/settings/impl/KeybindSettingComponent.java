package star.sequoia2.gui.component.settings.impl;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.gui.component.settings.SettingComponent;
import star.sequoia2.settings.Binding;
import star.sequoia2.settings.types.KeybindSetting;

import java.awt.*;

public class KeybindSettingComponent extends SettingComponent<Binding> {
    private boolean listening;

    public KeybindSettingComponent(KeybindSetting setting) {
        super(setting);
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();
        float right = left + contentWidth();
        float bottom = top + contentHeight();

        final Binding binding = getSetting().get();
        String val;
        if (listening) {
            val = "...";
        } else if (!binding.isSet()) {
            val = "None";
        } else {
            val = binding.name();
        }

        renderText(context, getSetting().name + " " + val, left, top, Color.white.getRGB());
    }

    @Override
    public void mouseMoved(float mouseX, float mouseY) {

    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int button) {

    }

    @Override
    public void mouseScrolled(float mouseX, float mouseY, double horizontalAmount, double verticalAmount) {

    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char chr, int modifiers) {

    }
}
