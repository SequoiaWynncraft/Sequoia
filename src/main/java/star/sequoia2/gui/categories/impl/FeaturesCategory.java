package star.sequoia2.gui.categories.impl;

import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.SettingsAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.features.Feature;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.gui.component.ModuleButton;
import star.sequoia2.gui.component.SearchBarComponent;
import star.sequoia2.gui.screen.GuiRoot;

import java.util.ArrayList;
import java.util.List;

public class FeaturesCategory extends RelativeComponent implements RenderUtilAccessor, FeaturesAccessor, TextRendererAccessor, SettingsAccessor {
    private final List<ModuleButton> moduleButtons = new ArrayList<>();
    SearchBarComponent searchBarComponent;

    private float scrollOffset = 0f;
    private boolean draggingScrollbar = false;

    public FeaturesCategory() {
        super("Features");
        for (Feature feature : features().all().toList()) {
            moduleButtons.add(new ModuleButton(feature));
        }
        searchBarComponent = new SearchBarComponent();
    }

    private float totalContentHeight(GuiRoot root) {
        float h = 0f;
        for (ModuleButton b : moduleButtons) {
            if (searchBarComponent.isSearching() && !b.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            h += root.btnH * 2 + root.btnGap;
            if (b.isOpen()) h += b.getExpandedHeight();
        }
        return h + root.btnGap;
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();
        float right = left + contentWidth();

        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color normal = features().get(Settings.class).map(Settings::getThemeNormal).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());
        Color accent1 = features().get(Settings.class).map(Settings::getThemeAccent1).orElse(Color.black());
        Color accent2 = features().get(Settings.class).map(Settings::getThemeAccent2).orElse(Color.black());

        GuiRoot root = getGuiRoot();;

        if (root == null) {
            render2DUtil().drawText(context, "couldn't access root", left + 5f, top + 5f, light.getColor(), true);
            return;
        }

        float trackPad = 6f;
        float trackW = 3f;

        searchBarComponent.render(context, mouseX, mouseY, delta);
        searchBarComponent.setPos(left, top);
        searchBarComponent.setDimensions(contentWidth() - trackW - 4f, getGuiRoot().btnH);

        float viewportX = left;
        float viewportY = top + root.btnH;
        float viewportW = contentWidth() - trackW - 4f;
        float viewportH = contentHeight() - root.btnH;

        float trackX = right - trackW;
        float trackY = viewportY + trackPad;
        float trackH = Math.max(0f, viewportH - trackPad * 2f);

        float totalContent = totalContentHeight(root);
        float maxOffset = Math.max(0f, totalContent - viewportH);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        if (scrollOffset < 0f) scrollOffset = 0f;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        context.enableScissor((int) viewportX, (int) viewportY, (int) (viewportX + viewportW), (int) (viewportY + viewportH));

        float drawOffset = 0f;
        for (ModuleButton button : moduleButtons) {
            if (searchBarComponent.isSearching() && !button.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            float itemH = root.btnH * 2;
            float y = viewportY + drawOffset - scrollOffset + root.btnGap;
            button.setPos(left, y);
            button.setDimensions(viewportW, itemH);
            button.render(context, mouseX, mouseY, delta);

            drawOffset += itemH + root.btnGap;
            if (button.isOpen()) {
                float eh = button.getExpandedHeight();
                drawOffset += eh;
            }
        }

        context.disableScissor();
        matrices.pop();

        if (totalContent > viewportH && trackH > 0f) {
            render2DUtil().roundGradientFilled(matrices, trackX, trackY, trackX + trackW, trackY + trackH, root.rounding, dark, normal, true);

            float thumbMinSize = 20f; // scroll thing tab  thing on the right
            float thumbH = Math.max(thumbMinSize, (viewportH / totalContent));
            float thumbY = trackY + (maxOffset == 0 ? 0 : (scrollOffset / maxOffset) * (trackH - thumbH));

            boolean overThumb = mouseX >= trackX && mouseX <= trackX + trackW && mouseY >= thumbY && mouseY <= thumbY + thumbH;
            Color tStart = overThumb || draggingScrollbar ? accent1 : light;
            Color tEnd = overThumb || draggingScrollbar ? accent2 : normal;

            render2DUtil().roundGradientFilled(matrices, trackX, thumbY, trackX + trackW, thumbY + thumbH, root.rounding, tStart, tEnd, true);
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        //fuckass scroll bar

        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.mouseClicked(mouseX, mouseY, button);
        }
        searchBarComponent.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(float mouseX, float mouseY) {
        //fuckass scroll bar

        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int button) {
        //fuckass scroll bar

        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseScrolled(float mouseX, float mouseY, double horizontalAmount, double verticalAmount) {
        GuiRoot root = getGuiRoot();
        if (root != null) {
            float viewportH = contentHeight() - root.btnH;
            float totalContent = totalContentHeight(root);
            float maxOffset = Math.max(0f, totalContent - viewportH);
            float step = root.btnH * 0.5f;
            scrollOffset = Math.max(0f, Math.min(maxOffset, scrollOffset - (float) verticalAmount * step));
        }

        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        searchBarComponent.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_ENTER && searchBarComponent.isSearching()) {
            searchBarComponent.setSearching(false);
            searchBarComponent.setSearch("");
        }
        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        searchBarComponent.charTyped(chr, modifiers);
        for (ModuleButton moduleButton : moduleButtons) {
            if (searchBarComponent.isSearching() && !moduleButton.name.toLowerCase().contains(searchBarComponent.getSearch().toLowerCase())) continue;
            moduleButton.charTyped(chr, modifiers);
        }
    }
}
