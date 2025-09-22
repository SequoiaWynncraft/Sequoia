package star.sequoia2.gui.component.settings.impl;

import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.component.settings.SettingComponent;
import star.sequoia2.settings.Setting;
import star.sequoia2.settings.types.CalculatedEnumSetting;

import java.util.List;

public class CalculatedEnumSettingComponent extends SettingComponent<String> implements RenderUtilAccessor {
    private int index;

    public CalculatedEnumSettingComponent(Setting<String> setting) {
        super(setting);
        if (setting instanceof CalculatedEnumSetting<?> calculatedSetting) {
            index = calculatedSetting.getOptions().indexOf(calculatedSetting.get());
            if (index == -1) {
                index = 0;
            }
        }
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();

        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());

        float horizontalPos = left + textRenderer().getWidth(setting.name) + getGuiRoot().pad;
        float horizontalPos2 = horizontalPos + textRenderer().getWidth(setting.get());
        render2DUtil().roundRectFilled(context.getMatrices(), horizontalPos, top, horizontalPos2, top + textRenderer().fontHeight, 1, dark);

        renderText(context, setting.name, left, top, light.getColor());
        renderText(context, setting.get(), horizontalPos, top, light.getColor());
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (isWithin(mouseX, mouseY) && setting instanceof CalculatedEnumSetting<?> calculatedSetting) {
            List<String> values = calculatedSetting.getOptions();
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                index = index + 1 > values.size() - 1 ? 0 : index + 1;
                setting.set(values.get(index));
            }
        }
    }
}
