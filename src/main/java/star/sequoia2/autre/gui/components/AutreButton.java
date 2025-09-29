package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * A clickable button component with flat design
 */
public class AutreButton extends AutreComponent {
    protected String text;
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color hoverColor;
    protected AutreRenderer2.Color pressedColor;
    protected AutreRenderer2.Color textColor;
    protected AutreRenderer2.Color disabledColor;
    protected AutreRenderer2.Color borderColor;
    protected float cornerRadius;
    protected float borderWidth = 1f;
    
    protected boolean pressed = false;
    
    public AutreButton(float x, float y, float width, float height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.backgroundColor = AutreRenderer2.Color.SURFACE;
        this.hoverColor = backgroundColor.lighter(0.1f);
        this.pressedColor = backgroundColor.darker(0.1f);
        this.textColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.disabledColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
        this.borderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.3f);
        this.cornerRadius = 0f; // Sharp edges by default
        
        // Set up event handlers
        onClickStart(event -> {
            pressed = true;
        });
        
        onClickRelease(event -> {
            pressed = false;
        });
    }
    
    public AutreButton setText(String text) {
        this.text = text;
        return this;
    }
    
    public AutreButton setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        this.hoverColor = color.lighter(0.1f);
        this.pressedColor = color.darker(0.1f);
        return this;
    }
    
    public AutreButton setTextColor(AutreRenderer2.Color color) {
        this.textColor = color;
        return this;
    }
    
    public AutreButton setCornerRadius(float radius) {
        this.cornerRadius = radius;
        return this;
    }
    
            @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Check if mouse is hovering
        boolean isHovered = enabled && mouseX >= getAbsoluteX() && mouseX <= getAbsoluteX() + width &&
                           mouseY >= getAbsoluteY() && mouseY <= getAbsoluteY() + height;
        
        // Determine colors based on state
        AutreRenderer2.Color currentBgColor;
        AutreRenderer2.Color currentTextColor;
        
        if (!enabled) {
            currentBgColor = disabledColor;
            currentTextColor = disabledColor.darker(0.3f);
        } else if (isHovered) {
            currentBgColor = backgroundColor.lighter(0.1f);
            currentTextColor = textColor.lighter(0.1f);
        } else {
            currentBgColor = backgroundColor;
            currentTextColor = textColor;
        }
        
        // Render background - flat design, no borders by default
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, currentBgColor);
        
        // Only render border if explicitly set (minimal flat design)
        if (borderWidth > 0f) {
            AutreRenderer2.Color currentBorderColor = isHovered ? borderColor.lighter(0.2f) : borderColor;
            // Top border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), width, borderWidth, currentBorderColor);
            // Bottom border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY() + height - borderWidth, width, borderWidth, currentBorderColor);
            // Left border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), borderWidth, height, currentBorderColor);
            // Right border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX() + width - borderWidth, getAbsoluteY(), borderWidth, height, currentBorderColor);
        }
        
        // Render text
        if (text != null && !text.isEmpty()) {
            float textWidth = mc.textRenderer.getWidth(text);
            float textX = getAbsoluteX() + (width - textWidth) / 2f;
            float textY = getAbsoluteY() + (height - mc.textRenderer.fontHeight) / 2f;
            
            context.drawText(mc.textRenderer, text,
                (int) textX, (int) textY,
                currentTextColor.toRGB(), false);
        }
    }
}