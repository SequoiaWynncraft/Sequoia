package star.sequoia2.gui.component.settings.impl;

import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.component.settings.SettingComponent;
import star.sequoia2.settings.Setting;
import star.sequoia2.settings.types.NumberSetting;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderComponent<T extends Number> extends SettingComponent<T> implements RenderUtilAccessor, TextRendererAccessor {
    private final int scale;
    private float animatedFill = 0f;
    private boolean isListening = false;
    private boolean dragging = false;
    private char[] buffer = new char[0];
    private boolean idling = false;
    private float blinkTimer = 0f;

    public SliderComponent(Setting<T> setting) {
        super(setting);
        if (setting instanceof NumberSetting<?> ns) {
            if (ns.get() instanceof Integer) {
                scale = 0;
            } else {
                int s = 2;
                try {
                    s = ns.scale;
                } catch (Exception ignored) {}
                scale = Math.max(0, s);
            }
        } else {
            scale = 0;
        }
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();
        float pad = getGuiRoot().pad;
        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());
        Color accent2 = features().get(Settings.class).map(Settings::getThemeAccent2).orElse(Color.black());

        renderText(context, setting.name, left, top, light.getColor());

        String valueStr = isListening ? new String(buffer) : formatValue(setting.get());
        float labelW = textRenderer().getWidth(setting.name);
        float fontH = textRenderer().fontHeight;

        float valueBoxX1 = left + labelW + pad;
        float valueBoxW = Math.max(textRenderer().getWidth(valueStr) + pad * 2f, 28f);
        float valueBoxX2 = valueBoxX1 + valueBoxW;
        float valueBoxY1 = top;
        float valueBoxY2 = top + fontH;

        render2DUtil().roundRectFilled(context.getMatrices(), valueBoxX1, valueBoxY1, valueBoxX2, valueBoxY2, 3f, dark);
        renderText(context, valueStr + (isListening ? getIdleSign(delta) : ""), valueBoxX1 + pad, top, light.getColor());

        float trackX1 = valueBoxX2 + pad;
        float trackX2 = Math.max(trackX1 + 40f, left + getWidth() - 2f);
        float trackY1 = top + fontH * 0.35f;
        float trackY2 = top + fontH * 0.65f;
        float trackH = trackY2 - trackY1;

        render2DUtil().roundRectFilled(context.getMatrices(), trackX1, trackY1, trackX2, trackY1 + trackH, trackH / 2f, dark);

        if (setting instanceof NumberSetting<?> ns) {
            float min = ns.min.floatValue();
            float max = ns.max.floatValue();
            float cur = ns.get().floatValue();
            float targetFill = (cur - min) / (max - min);
            animatedFill += (targetFill - animatedFill) * 0.3f * Math.max(0.016f, delta);
            animatedFill = MathHelper.clamp(animatedFill, 0f, 1f);

            float fillX2 = trackX1 + (trackX2 - trackX1) * animatedFill;
            render2DUtil().roundRectFilled(context.getMatrices(), trackX1, trackY1, fillX2, trackY1 + trackH, trackH / 2f, new Color(accent2.getRed(), accent2.getGreen(), accent2.getBlue(), 150));

            float knobW = Math.max(fontH * 0.8f, 6f);
            float knobH = Math.max(fontH * 0.9f, trackH + 4f);
            float knobX1 = MathHelper.clamp(fillX2 - knobW / 2f, trackX1, trackX2 - knobW);
            float knobY1 = top + (fontH - knobH) / 2f;
            render2DUtil().drawGlow(context, knobX1, knobY1, knobX1 + knobW, knobY1 + knobH, accent2, knobH / 2f);
            render2DUtil().roundRectFilled(context.getMatrices(), knobX1, knobY1, knobX1 + knobW, knobY1 + knobH, knobH / 2f, accent2);

            if (dragging && !isListening) {
                float fx = MathHelper.clamp((mouseX - trackX1) / (trackX2 - trackX1), 0f, 1f);
                applyValueFromFraction(ns, fx);
            }
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        float left = contentX();
        float top = contentY();
        float pad = getGuiRoot().pad;
        float labelW = textRenderer().getWidth(setting.name);
        float fontH = textRenderer().fontHeight;

        String valueStr = isListening ? new String(buffer) : formatValue(setting.get());
        float valueBoxX1 = left + labelW + pad;
        float valueBoxW = Math.max(textRenderer().getWidth(valueStr) + pad * 2f, 28f);
        float valueBoxX2 = valueBoxX1 + valueBoxW;
        float valueBoxY1 = top;
        float valueBoxY2 = top + fontH;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isWithin(mouseX, mouseY, valueBoxX1, valueBoxY1, valueBoxX2, valueBoxY2)) {
                isListening = true;
                buffer = formatValue(setting.get()).toCharArray();
                blinkTimer = 0f;
                idling = true;
                return;
            }
            float trackX1 = valueBoxX2 + pad;
            float trackX2 = Math.max(trackX1 + 40f, left + getWidth() - 2f);
            float trackY1 = top + fontH * 0.35f;
            float trackY2 = top + fontH * 0.65f;
            if (isWithin(mouseX, mouseY, trackX1, trackY1 - 6f, trackX2, trackY2 + 6f)) {
                dragging = true;
                if (setting instanceof NumberSetting<?> ns) {
                    float fx = MathHelper.clamp((mouseX - trackX1) / (trackX2 - trackX1), 0f, 1f);
                    applyValueFromFraction(ns, fx);
                }
            }
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = false;
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
        if (Character.isDigit(chr)) {
            buffer = ArrayUtils.add(buffer, chr);
        } else if (chr == '.') {
            if (new String(buffer).indexOf('.') == -1) {
                buffer = ArrayUtils.add(buffer, chr);
            }
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

    private void applyValueFromFraction(NumberSetting<?> ns, float frac) {
        Number minN = ns.min;
        Number maxN = ns.max;
        if (ns.get() instanceof Integer) {
            float v = minN.floatValue() + frac * (maxN.intValue() - minN.intValue());
            int bv = (int) MathHelper.clamp(v, minN.intValue(), maxN.intValue());
            ((NumberSetting<Integer>) ns).set(bv);
        } else if (ns.get() instanceof Float) {
            float v = minN.floatValue() + frac * (maxN.floatValue() - minN.floatValue());
            float bv = MathHelper.clamp(v, minN.floatValue(), maxN.floatValue());
            BigDecimal bd = new BigDecimal(bv);
            bv = bd.setScale(scale, RoundingMode.HALF_UP).floatValue();
            ((NumberSetting<Float>) ns).set(bv);
        } else if (ns.get() instanceof Double) {
            double v = minN.doubleValue() + frac * (maxN.doubleValue() - minN.doubleValue());
            double bv = MathHelper.clamp(v, minN.doubleValue(), maxN.doubleValue());
            BigDecimal bd = new BigDecimal(bv);
            bv = bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
            ((NumberSetting<Double>) ns).set(bv);
        }
    }

    private void deleteChar() {
        if (buffer.length == 0) return;
        buffer = ArrayUtils.remove(buffer, buffer.length - 1);
    }

    private void updateValue() {
        try {
            String input = new String(buffer).trim();
            if (input.isEmpty()) {
                isListening = false;
                return;
            }
            if (setting instanceof NumberSetting<?> ns) {
                if (ns.get() instanceof Integer) {
                    int v = Integer.parseInt(input);
                    int c = MathHelper.clamp(v, ((NumberSetting<Integer>) ns).min.intValue(), ((NumberSetting<Integer>) ns).max.intValue());
                    ((NumberSetting<Integer>) ns).set(c);
                } else if (ns.get() instanceof Float) {
                    float v = Float.parseFloat(input);
                    float c = MathHelper.clamp(v, ((NumberSetting<Float>) ns).min.floatValue(), ((NumberSetting<Float>) ns).max.floatValue());
                    BigDecimal bd = new BigDecimal(c);
                    c = bd.setScale(scale, RoundingMode.HALF_UP).floatValue();
                    ((NumberSetting<Float>) ns).set(c);
                } else if (ns.get() instanceof Double) {
                    double v = Double.parseDouble(input);
                    double c = MathHelper.clamp(v, ((NumberSetting<Double>) ns).min.doubleValue(), ((NumberSetting<Double>) ns).max.doubleValue());
                    BigDecimal bd = new BigDecimal(c);
                    c = bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
                    ((NumberSetting<Double>) ns).set(c);
                }
            }
        } catch (NumberFormatException ignored) {}
        isListening = false;
    }

    private void resetValue() {
        buffer = formatValue(setting.get()).toCharArray();
        isListening = false;
    }

    private String formatValue(Number n) {
        if (n instanceof Integer || scale <= 0) return String.valueOf(n);
        BigDecimal bd = new BigDecimal(n.doubleValue()).setScale(scale, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toPlainString();
    }

    private boolean isWithin(double mx, double my, float x1, float y1, float x2, float y2) {
        return mx >= x1 && mx <= x2 && my >= y1 && my <= y2;
    }
}
