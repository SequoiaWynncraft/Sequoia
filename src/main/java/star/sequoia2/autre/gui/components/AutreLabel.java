package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Simple text label component for displaying static text
 */
public class AutreLabel extends AutreComponent {
    protected String text;
    protected AutreRenderer2.Color textColor;
    protected boolean centered;
    protected boolean shadow;
    
    public AutreLabel(float x, float y, float width, float height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.textColor = new AutreRenderer2.Color(255, 255, 255, 255); // White by default
        this.centered = true;
        this.shadow = false;
    }
    
    public AutreLabel(float x, float y, String text) {
        super(x, y, 0, 0);
        this.text = text;
        this.textColor = new AutreRenderer2.Color(255, 255, 255, 255);
        this.centered = false;
        this.shadow = false;
        
        // Auto-size to text
        if (text != null && !text.isEmpty()) {
            this.width = mc.textRenderer.getWidth(text);
            this.height = mc.textRenderer.fontHeight;
        }
    }
    
    public AutreLabel setText(String text) {
        this.text = text;
        
        // Auto-resize if needed
        if (!centered && text != null && !text.isEmpty()) {
            this.width = mc.textRenderer.getWidth(text);
            this.height = mc.textRenderer.fontHeight;
        }
        
        return this;
    }
    
    public AutreLabel setTextColor(AutreRenderer2.Color color) {
        this.textColor = color;
        return this;
    }
    
    public AutreLabel setCentered(boolean centered) {
        this.centered = centered;
        return this;
    }
    
    public AutreLabel setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }
    
    public String getText() {
        return text;
    }
    
    public AutreRenderer2.Color getTextColor() {
        return textColor;
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (text == null || text.isEmpty()) return;
        
        float textWidth = mc.textRenderer.getWidth(text);
        float textHeight = mc.textRenderer.fontHeight;
        
        float textX, textY;
        
        if (centered) {
            textX = getAbsoluteX() + (width - textWidth) / 2;
            textY = getAbsoluteY() + (height - textHeight) / 2;
        } else {
            textX = getAbsoluteX();
            textY = getAbsoluteY();
        }
        
        if (shadow) {
            // Render shadow
            AutreRenderer2.Color shadowColor = textColor.darker(0.8f);
            context.drawText(mc.textRenderer, text, (int) textX + 1, (int) textY + 1, 
                shadowColor.toRGB(), false);
        }
        
        // Render main text
        context.drawText(mc.textRenderer, text, (int) textX, (int) textY, 
            textColor.toRGB(), false);
    }
}