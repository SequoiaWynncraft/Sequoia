package star.sequoia2.gui.categories;

import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.gui.component.InteractableComponent;
import star.sequoia2.gui.screen.GuiRoot;
import star.sequoia2.features.impl.Client;

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
            return features().get(Client.class).map(Client::getClickGui).orElseThrow().getRoot();
        } catch (Exception e) {
            return null;
        }
    }
}
