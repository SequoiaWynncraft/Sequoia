package star.sequoia2.gui.component.settings.impl;

import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.component.settings.SettingComponent;
import star.sequoia2.settings.Setting;

public class TextInputSettingComponent extends SettingComponent<String> implements RenderUtilAccessor, TextRendererAccessor {
    public boolean isListening;
    private char[] buffer;
    private boolean idling = false;
    private float blinkTimer = 0f;

    public TextInputSettingComponent(Setting<String> setting) {
        super(setting);
        buffer = setting.get() != null ? setting.get().toCharArray() : new char[0];
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();
        float pad = getGuiRoot().pad;
        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());

        renderText(context, setting.name, left, top, light.getColor());

        String valueStr = isListening ? new String(buffer) : (setting.get() == null ? "" : setting.get());
        float labelW = textRenderer().getWidth(setting.name);
        float fontH = textRenderer().fontHeight;

        float valueBoxX1 = left + labelW + pad;
        float valueBoxW = Math.max(textRenderer().getWidth(valueStr) + pad * 2f, 60f);
        float valueBoxX2 = valueBoxX1 + valueBoxW;
        float valueBoxY1 = top;
        float valueBoxY2 = top + fontH;

        render2DUtil().roundRectFilled(context.getMatrices(), valueBoxX1, valueBoxY1, valueBoxX2, valueBoxY2, 3f, dark);
        renderText(context, valueStr + (isListening ? getIdleSign(delta) : ""), valueBoxX1 + pad, top, light.getColor());
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        float left = contentX();
        float top = contentY();
        float pad = getGuiRoot().pad;
        float labelW = textRenderer().getWidth(setting.name);
        float fontH = textRenderer().fontHeight;

        String valueStr = isListening ? new String(buffer) : (setting.get() == null ? "" : setting.get());
        float valueBoxX1 = left + labelW + pad;
        float valueBoxW = Math.max(textRenderer().getWidth(valueStr) + pad * 2f, 60f);
        float valueBoxX2 = valueBoxX1 + valueBoxW;
        float valueBoxY1 = top;
        float valueBoxY2 = top + fontH;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isWithin(mouseX, mouseY, valueBoxX1, valueBoxY1, valueBoxX2, valueBoxY2)) {
                isListening = true;
                buffer = (setting.get() == null ? "" : setting.get()).toCharArray();
                blinkTimer = 0f;
                idling = true;
            } else {
                isListening = false;
            }
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isListening) {
            super.keyPressed(keyCode, scanCode, modifiers);
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            updateValue();
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            deleteChar();
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            resetValue();
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        if (!isListening) return;
        if (!Character.isISOControl(chr)) {
            buffer = ArrayUtils.add(buffer, chr);
        }
    }

    private String getIdleSign(float delta) {
        blinkTimer += Math.max(0.016f, delta);
        if (blinkTimer >= 5f) {
            idling = !idling;
            blinkTimer = 0f;
        }
        return idling ? "_" : "";
    }

    private void deleteChar() {
        if (buffer.length == 0) return;
        buffer = ArrayUtils.remove(buffer, buffer.length - 1);
    }

    private void updateValue() {
        String input = new String(buffer).trim();
        setting.set(input);
        isListening = false;
    }

    private void resetValue() {
        buffer = (setting.get() == null ? "" : setting.get()).toCharArray();
        isListening = false;
    }

    private boolean isWithin(double mx, double my, float x1, float y1, float x2, float y2) {
        return mx >= x1 && mx <= x2 && my >= y1 && my <= y2;
    }
}
