package star.sequoia2.gui.screen;

import lombok.Getter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.categories.RelativeComponent;
import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import star.sequoia2.utils.render.TextureStorage;

import java.util.List;

public final class GuiRoot implements RenderUtilAccessor, TextRendererAccessor, FeaturesAccessor {

    @Getter
    private final List<RelativeComponent> categories;
    @Getter
    private int selected = 0;

    private float uiW, uiH;
    private float x, y;
    private float w, h;

    public final float boxHeight = 300;
    public final float boxWidth = 380;
    public final float pad = 5f;
    public final float btnW = 50f;
    public final float btnH = 20f;
    public final float btnGap = 3f;
    public final float rounding = 6f;

    public GuiRoot(List<RelativeComponent> categories) {
        this.categories = categories;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(float width, float height) {
        this.w = width;
        this.h = height;
    }

    public void layout(float uiWidth, float uiHeight) {
        this.uiW = uiWidth;
        this.uiH = uiHeight;
    }

    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();
        float bx = (uiW - boxWidth) / 2f;
        float by = (uiH - boxHeight) / 2f;

        Color normal = features().get(Settings.class).map(Settings::getThemeNormal).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());
        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color accent1 = features().get(Settings.class).map(Settings::getThemeAccent1).orElse(Color.black());
        Color accent2 = features().get(Settings.class).map(Settings::getThemeAccent2).orElse(Color.black());
        Color accent3 = features().get(Settings.class).map(Settings::getThemeAccent3).orElse(Color.black());

        render2DUtil().roundGradientFilled(matrices, bx, by, bx + boxWidth, by + boxHeight, rounding, normal, dark, true);

        matrices.push();
        render2DUtil().enableScissor((int) bx, (int) by, (int) (bx + btnW + pad * 2), (int) (by + boxHeight));
        render2DUtil().roundGradientFilled(matrices, bx, by, bx + boxWidth, by + boxHeight, rounding, dark, normal, true);
        render2DUtil().disableScissor();
        matrices.pop();

        matrices.push();
        matrices.translate(bx + pad, by + pad, 0);
        context.drawTexture(RenderLayer::getGuiTextured , TextureStorage.icon, 0, 0, 0, 0, (int) btnW, (int) btnW, (int) btnW, (int) btnW);
        matrices.pop();

        float listX = bx + pad;
        float listY = by + btnW + pad * 2;

        for (int i = 0; i < categories.size(); i++) {
            RelativeComponent c = categories.get(i);
            float byTop = listY + i * (btnH + btnGap);
            boolean hover = mouseX >= listX && mouseX <= listX + btnW && mouseY >= byTop && mouseY <= byTop + btnH;

            Color start, end;
            if (i == selected) {
                start = accent1;
                end = accent2;
            } else if (hover) {
                start = light;
                end = normal;
            } else {
                start = dark;
                end = dark;
            }

            render2DUtil().roundGradientFilled(matrices, listX, byTop, listX + btnW, byTop + btnH, rounding, start, end, true);

            int textWidth = textRenderer().getWidth(c.name);
            float textX = listX + (btnW - textWidth) / 2f;
            float textY = byTop + (btnH - textRenderer().fontHeight) / 2f;

            Color glowColor = accent2;

            if (i == selected) {
                render2DUtil().drawGlow(context, listX, byTop, listX + btnW, byTop + btnH, glowColor, rounding);
            }

            context.drawText(textRenderer(), c.name, (int) textX, (int) textY, light.getColor(), true);
        }

        float contentX = bx + pad + btnW + pad * 2;
        float contentY = by + pad;
        float contentW = boxWidth - (contentX - bx) - pad;
        float contentH = boxHeight - pad * 2f;

        if (!categories.isEmpty()) {
            RelativeComponent current = categories.get(selected);
            current.setPos(contentX, contentY);
            current.setDimensions(contentW, contentH);
            current.render(context, mouseX, mouseY, delta);
        }
    }

    public void mouseMoved(float mouseX, float mouseY) {
        if (categories.isEmpty()) return;
        RelativeComponent current = categories.get(selected);
        float bx = (uiW - boxWidth) / 2f;
        float by = (uiH - boxHeight) / 2f;
        current.mouseMoved(mouseX - bx, mouseY - by);
    }

    public void mouseClicked(float mouseX, float mouseY, int button) {
        float bx = (uiW - boxWidth) / 2f;
        float by = (uiH - boxHeight) / 2f;

        float listX = bx + pad;
        float listY = by + btnW + pad * 2;

        for (int i = 0; i < categories.size(); i++) {
            float byTop = listY + i * (btnH + btnGap);
            if (mouseX >= listX && mouseX <= listX + btnW && mouseY >= byTop && mouseY <= byTop + btnH) {
                selected = i;
                return;
            }
        }

        if (categories.isEmpty()) return;
        categories.get(selected).mouseClicked(mouseX, mouseY, button);
    }

    public void mouseReleased(float mouseX, float mouseY, int button) {
        if (categories.isEmpty()) return;
        float bx = (uiW - boxWidth) / 2f;
        float by = (uiH - boxHeight) / 2f;
        categories.get(selected).mouseReleased(mouseX - bx, mouseY - by, button);
    }

    public void mouseScrolled(float mouseX, float mouseY, double horizontalAmount, double verticalAmount) {
        if (categories.isEmpty()) return;
        float bx = (uiW - boxWidth) / 2f;
        float by = (uiH - boxHeight) / 2f;
        categories.get(selected).mouseScrolled(mouseX - bx, mouseY - by, horizontalAmount, verticalAmount);
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (categories.isEmpty()) return;
        categories.get(selected).keyPressed(keyCode, scanCode, modifiers);
    }

    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (categories.isEmpty()) return;
        categories.get(selected).keyReleased(keyCode, scanCode, modifiers);
    }

    public void charTyped(char chr, int modifiers) {
        if (categories.isEmpty()) return;
        categories.get(selected).charTyped(chr, modifiers);
    }
}
