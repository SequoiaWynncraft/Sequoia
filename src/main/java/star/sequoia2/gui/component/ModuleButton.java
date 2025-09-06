package star.sequoia2.gui.component;

import com.mojang.logging.LogUtils;
import star.sequoia2.accessors.ConfigurationAccessor;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.SettingsAccessor;
import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.gui.component.settings.SettingComponent;
import star.sequoia2.gui.component.settings.impl.KeybindSettingComponent;
import star.sequoia2.gui.screen.GuiRoot;
import star.sequoia2.features.Feature;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.settings.Setting;
import lombok.Getter;
import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import star.sequoia2.settings.types.KeybindSetting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleButton extends RelativeComponent implements SettingsAccessor, FeaturesAccessor, RenderUtilAccessor, ConfigurationAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Feature feature;
    private List<SettingComponent<?>> settingComponents = new CopyOnWriteArrayList<>();
    @Getter
    private boolean open = false;

    public ModuleButton(Feature feature) {
        super(feature.name);
        this.feature = feature;
        settingComponents = createComponents(feature);
    }

    private List<SettingComponent<?>> createComponents(Feature feature) {
        List<SettingComponent<?>> components = new CopyOnWriteArrayList<>();
        for (Setting<?> setting : settingsState().fromFeature(feature).all().toList()) {
            addSettingsComponents(components, setting);
        }
        return components;
    }

    private void addSettingsComponents(List<SettingComponent<?>> components, Setting<?> setting) {
        if (setting instanceof KeybindSetting keybindSetting) {
            components.add(new KeybindSettingComponent(keybindSetting));
        }
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        GuiRoot root = features().get(Settings.class).map(Settings::getClickGui).orElseThrow().getRoot();

        float left = contentX();
        float top = contentY();
        float right = left + contentWidth();
        float bottom = getCurrentBottom();

        boolean hovering = mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;

        Color bg = hovering ? new Color(18, 18, 18) : new Color(20, 20, 20);
        render2DUtil().roundRectFilled(context.getMatrices(), left, top, right, bottom, root.rounding, bg);

        context.getMatrices().push();
        context.getMatrices().translate(left + root.pad, top + textRenderer().fontHeight, 0);
        context.getMatrices().scale(1.1f, 1.1f, 0);

        int textColor;
        if (feature instanceof ToggleFeature toggleModule) {
            textColor = toggleModule.isActive() ? new Color(100, 100, 250).getColor() : Color.white().getColor();
        } else {
            textColor = Color.white().getColor();
        }
        renderText(context, feature.name, 0, 0, textColor, true);
        context.getMatrices().pop();

        renderText(context, "§7" + feature.description, left + root.pad, top + contentHeight() / 2f, Color.white().getColor(), true);

        String symbol = open ? "-" : "+";
        renderText(context, symbol, right - root.pad - textRenderer().getWidth(symbol), top + (contentHeight() - textRenderer().fontHeight) / 2f, Color.white().getColor(), true);

        if (open) {
            float offsetY = contentHeight() + root.btnGap * 0.5f;
            for (SettingComponent<?> settingComp : settingComponents) {
                settingComp.setPos(left + root.pad, top + offsetY);
                settingComp.setDimensions(contentWidth() - root.pad * 2f, root.btnH * 0.8f);
                settingComp.render(context, mouseX, mouseY, delta);
                offsetY += settingComp.contentHeight() + root.btnGap * 0.5f;
            }
        }
    }

    private float getCurrentBottom() {
        float base = contentY() + contentHeight();
        if (!open) return base;
        return base + getExpandedHeight();
    }

    public float getExpandedHeight() {
        GuiRoot root = features().get(Settings.class).map(Settings::getClickGui).orElseThrow().getRoot();
        float height = 0;
        for (SettingComponent<?> comp : settingComponents) {
            height += comp.contentHeight() + root.btnGap * 0.5f;
        }
        return height;
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (isWithinContent(mouseX, mouseY)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                open = !open;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && feature instanceof ToggleFeature toggleModule) {
                toggleModule.toggle();
                try {
                    configuration().save();
                } catch (IOException e) {
                    LOGGER.error("Could not save configuration", e);
                }
            }
            return;
        }

        if (open) {
            for (SettingComponent<?> comp : settingComponents) {
                if (comp.isWithinContent(mouseX, mouseY)) {
                    comp.mouseClicked(mouseX, mouseY, button);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseMoved(float mouseX, float mouseY) {

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
