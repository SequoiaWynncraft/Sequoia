package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * A basic panel component that renders a background
 */
public class AutrePanel extends AutreComponent {
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color borderColor;
    protected float borderThickness;
    protected float cornerRadius;
    
    public AutrePanel(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.backgroundColor = AutreRenderer2.Color.SURFACE;
        this.borderColor = AutreRenderer2.Color.SECONDARY;
        this.borderThickness = 0f;
        this.cornerRadius = 0f; // Sharp corners by default
    }
    
    public AutrePanel setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public AutrePanel setBorderColor(AutreRenderer2.Color color) {
        this.borderColor = color;
        return this;
    }
    
    public AutrePanel setBorderThickness(float thickness) {
        this.borderThickness = thickness;
        return this;
    }
    
    public AutrePanel setCornerRadius(float radius) {
        this.cornerRadius = radius;
        return this;
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (backgroundColor.a > 0) {
            // Always render as flat rectangle (no hover effects)
            AutreRenderer2.fillRect(context.getMatrices(), 
                getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        }
        
        // No borders for clean flat design
    }
}