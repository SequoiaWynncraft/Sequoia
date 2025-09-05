package star.sequoia2.gui.component;

import net.minecraft.client.gui.DrawContext;

public interface Drawable {
    void render(DrawContext context, float mouseX, float mouseY, float delta);
}
