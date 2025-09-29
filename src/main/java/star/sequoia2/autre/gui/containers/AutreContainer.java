package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * Basic container for holding other UI components with flat design
 */
public class AutreContainer extends AutreComponent {
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.BACKGROUND;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SURFACE;
    protected float borderWidth = 0f; // Flat design - no borders by default
    protected float padding = 8f;
    
    public AutreContainer(float x, float y, float width, float height) {
        super(x, y, width, height);
    }
    
    public AutreContainer setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public AutreContainer setBorderColor(AutreRenderer2.Color color) {
        this.borderColor = color;
        return this;
    }
    
    public AutreContainer setBorderWidth(float width) {
        this.borderWidth = width;
        return this;
    }
    
    public AutreContainer setPadding(float padding) {
        this.padding = padding;
        return this;
    }
    
    public float getPadding() {
        return padding;
    }
    
    public float getContentX() {
        return getAbsoluteX() + padding;
    }
    
    public float getContentY() {
        return getAbsoluteY() + padding;
    }
    
    public float getContentWidth() {
        return Math.max(0, width - (padding * 2));
    }
    
    public float getContentHeight() {
        return Math.max(0, height - (padding * 2));
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Render background - flat rectangle
        if (backgroundColor != null) {
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        }
        
        // Render border if specified (minimal for flat design)
        if (borderWidth > 0 && borderColor != null) {
            // Top border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), width, borderWidth, borderColor);
            // Bottom border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY() + height - borderWidth, width, borderWidth, borderColor);
            // Left border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), borderWidth, height, borderColor);
            // Right border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX() + width - borderWidth, getAbsoluteY(), borderWidth, height, borderColor);
        }
    }
}