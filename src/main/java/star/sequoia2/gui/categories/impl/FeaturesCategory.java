package star.sequoia2.gui.categories.impl;

import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.SettingsAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.gui.component.ModuleButton;
import star.sequoia2.gui.component.SearchBarComponent;
import star.sequoia2.gui.screen.GuiRoot;
import star.sequoia2.features.Feature;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class FeaturesCategory extends RelativeComponent implements RenderUtilAccessor, FeaturesAccessor, TextRendererAccessor, SettingsAccessor {
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    SearchBarComponent searchBarComponent;

    public boolean open = false;

    public FeaturesCategory() {
        super("Features");
        for (Feature feature : features().all().toList()) {
            moduleButtons.add(new ModuleButton(feature));
        }
        searchBarComponent = new SearchBarComponent();
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();

        GuiRoot root = getGuiRoot();;

        if (root == null) {
            render2DUtil().drawText(context, "couldn't access root", left + 5f, top + 5f, Color.white().getColor(), true);
            return;
        }

        searchBarComponent.render(context, mouseX, mouseY, delta);
        searchBarComponent.setPos(left, top);
        searchBarComponent.setDimensions(contentWidth(), getGuiRoot().btnH);

        // layout feature buttons vertically
        float offsetY = root.btnH + root.btnGap;
        for (ModuleButton button : moduleButtons) {
            if (searchBarComponent.isSearching() && !button.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            button.setPos(left, top + offsetY);
            button.setDimensions(contentWidth(), root.btnH * 2); // give each button height
            button.render(context, mouseX, mouseY, delta);

            offsetY += root.btnH * 2 + root.btnGap;

            if (button.isOpen()) {
                offsetY += button.getExpandedHeight(); // include expanded settings
            }
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (!isWithinContent(mouseX, mouseY)) return;

        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            if (moduleButton.isWithinContent(mouseX, mouseY)) {
                moduleButton.mouseClicked(mouseX, mouseY, button);
                return;
            }
        }
        searchBarComponent.mouseClicked(mouseX, mouseY, button);
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
        searchBarComponent.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_ENTER && searchBarComponent.isSearching()) {
            searchBarComponent.setSearching(false);
            searchBarComponent.setSearch("");
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char chr, int modifiers) {
        searchBarComponent.charTyped(chr, modifiers);
    }
}
