package star.sequoia2.settings;
import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.events.input.KeyEvent;
import star.sequoia2.events.input.MouseButtonEvent;
import org.apache.commons.lang3.StringUtils;

import static org.lwjgl.glfw.GLFW.*;

public record Binding(int key, int button) {

    private static final String NOT_SET = "NONE";

    public static Binding withButton(int button) {
        return new Binding(GLFW_KEY_UNKNOWN, button);
    }

    public static Binding withKey(int key) {
        return new Binding(key, -1);
    }

    public static Binding none() {
        return new Binding(GLFW_KEY_UNKNOWN, -1);
    }

    public static Binding from(JsonCompound json) {
        int key = -1;
        if (json.contains("key")) {
            key = json.getInt("key");
        }
        int button = -1;
        if (json.contains("button")) {
            button = json.getInt("button");
        }
        return new Binding(key, button);
    }

    public JsonCompound toJSON(JsonCompound json) {
        json.putInt("key", key);
        json.putInt("button", button);
        return json;
    }

    public boolean matches(KeyEvent event) {
        return key == event.key();
    }

    public boolean matches(MouseButtonEvent event) {
        return button == event.button();
    }

    public boolean isSet() {
        return button == -1 || key == -1;
    }

    public String name() {
        if (button > -1) {
            return switch (button) {
                case 0 -> "Left Click";
                case 1 -> "Right Click";
                default -> button + " Click";
            };
        }
        if (key > -1) {
            return switch (key) {
                case GLFW_KEY_ESCAPE -> "Esc";
                case GLFW_KEY_GRAVE_ACCENT -> "Grave Accent";
                case GLFW_KEY_WORLD_1 -> "World 1";
                case GLFW_KEY_WORLD_2 -> "World 2";
                case GLFW_KEY_PRINT_SCREEN -> "Print Screen";
                case GLFW_KEY_PAUSE -> "Pause";
                case GLFW_KEY_INSERT -> "Insert";
                case GLFW_KEY_DELETE -> "Delete";
                case GLFW_KEY_HOME -> "Home";
                case GLFW_KEY_PAGE_UP -> "Page Up";
                case GLFW_KEY_PAGE_DOWN -> "Page Down";
                case GLFW_KEY_END -> "End";
                case GLFW_KEY_TAB -> "Tab";
                case GLFW_KEY_LEFT_CONTROL -> "Left Control";
                case GLFW_KEY_RIGHT_CONTROL -> "Right Control";
                case GLFW_KEY_LEFT_ALT -> "Left Alt";
                case GLFW_KEY_RIGHT_ALT -> "Right Alt";
                case GLFW_KEY_LEFT_SHIFT -> "Left Shift";
                case GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
                case GLFW_KEY_UP -> "Arrow Up";
                case GLFW_KEY_DOWN -> "Arrow Down";
                case GLFW_KEY_LEFT -> "Arrow Left";
                case GLFW_KEY_RIGHT -> "Arrow Right";
                case GLFW_KEY_APOSTROPHE -> "Apostrophe";
                case GLFW_KEY_BACKSPACE -> "Backspace";
                case GLFW_KEY_CAPS_LOCK -> "Caps Lock";
                case GLFW_KEY_MENU -> "Menu";
                case GLFW_KEY_LEFT_SUPER -> "Left Super";
                case GLFW_KEY_RIGHT_SUPER -> "Right Super";
                case GLFW_KEY_ENTER -> "Enter";
                case GLFW_KEY_KP_ENTER -> "Numpad Enter";
                case GLFW_KEY_NUM_LOCK -> "Num Lock";
                case GLFW_KEY_SPACE -> "Space";
                case GLFW_KEY_F1 -> "F1";
                case GLFW_KEY_F2 -> "F2";
                case GLFW_KEY_F3 -> "F3";
                case GLFW_KEY_F4 -> "F4";
                case GLFW_KEY_F5 -> "F5";
                case GLFW_KEY_F6 -> "F6";
                case GLFW_KEY_F7 -> "F7";
                case GLFW_KEY_F8 -> "F8";
                case GLFW_KEY_F9 -> "F9";
                case GLFW_KEY_F10 -> "F10";
                case GLFW_KEY_F11 -> "F11";
                case GLFW_KEY_F12 -> "F12";
                case GLFW_KEY_F13 -> "F13";
                case GLFW_KEY_F14 -> "F14";
                case GLFW_KEY_F15 -> "F15";
                case GLFW_KEY_F16 -> "F16";
                case GLFW_KEY_F17 -> "F17";
                case GLFW_KEY_F18 -> "F18";
                case GLFW_KEY_F19 -> "F19";
                case GLFW_KEY_F20 -> "F20";
                case GLFW_KEY_F21 -> "F21";
                case GLFW_KEY_F22 -> "F22";
                case GLFW_KEY_F23 -> "F23";
                case GLFW_KEY_F24 -> "F24";
                case GLFW_KEY_F25 -> "F25";
                default -> {
                    String keyName = glfwGetKeyName(key, 0);
                    yield keyName == null ? NOT_SET : StringUtils.capitalize(keyName);
                }
            };
        }
        return NOT_SET;
    }
}
