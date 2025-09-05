package star.sequoia2.gui.screen;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.gui.categories.RelativeComponent;
import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import star.sequoia2.utils.render.TextureStorage;

import java.util.List;

public final class GuiRoot implements RenderUtilAccessor, TextRendererAccessor {

    private final List<RelativeComponent> categories;
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

        // main background box (centered)
        render2DUtil().roundRectFilled(matrices, bx, by, bx + boxWidth, by + boxHeight, rounding, new Color(30, 30, 30));

        //category box area border.
        matrices.push();
        render2DUtil().enableScissor((int) bx, (int) by, (int) (bx + btnW + pad * 2), (int) (by + boxHeight));
        render2DUtil().roundRectFilled(matrices, bx, by, bx + boxWidth, by + boxHeight, rounding, new Color(20, 20, 20));
        render2DUtil().disableScissor();
        matrices.pop();

        matrices.push();
        matrices.translate(bx + pad, by + pad, 0);
        context.drawTexture(RenderLayer::getGuiTextured , TextureStorage.icon, 0, 0, 0, 0, (int) btnW, (int) btnW, (int) btnW, (int) btnW);
        matrices.pop();

        // sidebar buttons
        float listX = bx + pad;
        float listY = by + btnW + pad * 2;

        for (int i = 0; i < categories.size(); i++) {
            RelativeComponent c = categories.get(i);
            float byTop = listY + i * (btnH + btnGap);
            boolean hover = mouseX >= listX && mouseX <= listX + btnW && mouseY >= byTop && mouseY <= byTop + btnH;
            Color bg = (i == selected) ? new Color(30, 50, 150) : hover ? new Color(30, 30, 30) : new Color(35, 35, 35, 10);

            render2DUtil().roundRectFilled(matrices, listX, byTop, listX + btnW, byTop + btnH, rounding, bg);

            int textWidth = textRenderer().getWidth(c.name);
            float textX = listX + (btnW - textWidth) / 2f;
            float textY = byTop + (btnH - textRenderer().fontHeight) / 2f;

            // Pick a glow color (same as your selected blue, or any accent)
            Color glowColor = new Color(40, 40, 100, 50);

            if (i == selected) {
                render2DUtil().drawGlow(context, listX, byTop, listX + btnW, byTop + btnH, glowColor, rounding);
            }

            context.drawText(textRenderer(), c.name, (int) textX, (int) textY, Color.white().getColor(), true);
        }

        // content area
        float contentX = bx + pad + btnW + pad * 2;
        float contentY = by + pad;
        float contentW = boxWidth - (contentX - bx) - pad;
        float contentH = boxHeight - pad * 2f;

        // render current category inside the content area
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
