package star.sequoia2.gui.component.settings;

import org.lwjgl.glfw.GLFW;
import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.settings.Setting;

public abstract class SettingComponent<T> extends RelativeComponent {
    protected final Setting<T> setting;

    protected boolean leftShiftHeld = false;

    public SettingComponent(Setting<T> setting) {
        super(setting.name);
        this.setting = setting;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            leftShiftHeld = true;
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            leftShiftHeld = false;
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (isWithin(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && leftShiftHeld) {
            setting.reset();
        }
    }
}
