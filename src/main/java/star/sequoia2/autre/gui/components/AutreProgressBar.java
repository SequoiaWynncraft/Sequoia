package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Flat progress bar component
 */
public class AutreProgressBar extends AutreComponent {
    protected float progress = 0f; // 0.0 to 1.0
    protected String label = "";
    protected boolean showLabel = true;
    protected boolean showPercentage = true;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color fillColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.getAccent().withAlpha(0.3f);
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    
    public AutreProgressBar(float x, float y, float width, float height) {
        super(x, y, width, height);
    }
    
    public AutreProgressBar setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        return this;
    }
    
    public float getProgress() {
        return progress;
    }
    
    public AutreProgressBar setLabel(String label) {
        this.label = label;
        return this;
    }
    
    public AutreProgressBar setShowLabel(boolean show) {
        this.showLabel = show;
        return this;
    }
    
    public AutreProgressBar setShowPercentage(boolean show) {
        this.showPercentage = show;
        return this;
    }
    
    public AutreProgressBar setFillColor(AutreRenderer2.Color color) {
        this.fillColor = color;
        return this;
    }
    
    public AutreProgressBar setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Background - flat rectangle
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        
        // Progress fill
        float fillWidth = width * progress;
        if (fillWidth > 0) {
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), fillWidth, height, fillColor);
        }
        
        // Optional border (minimal for flat design)
        if (borderColor != null) {
            // Top border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), width, 1f, borderColor);
            // Bottom border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY() + height - 1f, width, 1f, borderColor);
        }
        
        // Text overlay
        if ((showLabel && label != null && !label.isEmpty()) || showPercentage) {
            String displayText = "";
            
            if (showLabel && label != null && !label.isEmpty()) {
                displayText = label;
                if (showPercentage) {
                    displayText += " ";
                }
            }
            
            if (showPercentage) {
                displayText += String.format("%.0f%%", progress * 100);
            }
            
            int textWidth = mc.textRenderer.getWidth(displayText);
            int textHeight = mc.textRenderer.fontHeight;
            
            float textX = getAbsoluteX() + (width - textWidth) / 2;
            float textY = getAbsoluteY() + (height - textHeight) / 2;
            
            AutreRenderer2.drawText(context, displayText, textX, textY, textColor, false);
        }
    }
}