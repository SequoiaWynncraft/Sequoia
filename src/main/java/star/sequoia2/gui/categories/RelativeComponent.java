package star.sequoia2.gui.categories;

import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.component.InteractableComponent;
import star.sequoia2.gui.screen.GuiRoot;

public abstract class RelativeComponent extends InteractableComponent implements FeaturesAccessor {

    public String name;

    public RelativeComponent(String name) {
        this.name = name;
    }

    public float contentX() { return x; }
    public float contentY() { return y; }
    public float contentWidth() { return width; }
    public float contentHeight() { return height; }

    public float localMouseX(float mouseX) { return mouseX - x; }
    public float localMouseY(float mouseY) { return mouseY - y; }

    public boolean isWithinContent(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public GuiRoot getGuiRoot() {
        GuiRoot root;
        try {
            return features().get(Settings.class).map(Settings::getClickGui).orElseThrow().getRoot();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {

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
    public void charTyped(char chr, int modifiers) {

    }
}
