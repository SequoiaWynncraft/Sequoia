package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Card container for grouped content with flat design
 */
public class AutreCard extends AutreContainer {
    protected String title;
    protected float titleHeight = 30f;
    protected AutreRenderer2.Color titleBackgroundColor = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color titleTextColor = AutreRenderer2.Color.TEXT_PRIMARY;
    
    public AutreCard(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        // Card styling - flat with subtle accent
        setBackgroundColor(AutreRenderer2.Color.SURFACE);
        setBorderWidth(1f);
        setBorderColor(AutreRenderer2.Color.getAccent().withAlpha(0.2f));
    }
    
    public AutreCard setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public AutreCard setTitleHeight(float height) {
        this.titleHeight = height;
        return this;
    }
    
    public AutreCard setTitleBackgroundColor(AutreRenderer2.Color color) {
        this.titleBackgroundColor = color;
        return this;
    }
    
    public AutreCard setTitleTextColor(AutreRenderer2.Color color) {
        this.titleTextColor = color;
        return this;
    }
    
    @Override
    public float getContentY() {
        return super.getContentY() + (hasTitle() ? titleHeight : 0);
    }
    
    @Override
    public float getContentHeight() {
        float baseHeight = super.getContentHeight();
        return hasTitle() ? Math.max(0, baseHeight - titleHeight) : baseHeight;
    }
    
    private boolean hasTitle() {
        return title != null && !title.trim().isEmpty();
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Render background
        super.renderSelf(context, mouseX, mouseY, deltaTime);
        
        // Render title if present
        if (hasTitle()) {
            float titleY = getAbsoluteY() + padding;
            
            // Title background
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX() + padding, titleY, 
                getContentWidth(), titleHeight, titleBackgroundColor);
            
            // Title text
            float textWidth = mc.textRenderer.getWidth(title);
            float textX = getAbsoluteX() + padding + (getContentWidth() - textWidth) / 2;
            float textY = titleY + (titleHeight - mc.textRenderer.fontHeight) / 2;
            
            AutreRenderer2.drawText(context, title, textX, textY, titleTextColor, false);
            
            // Separator line (subtle)
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX() + padding, titleY + titleHeight - 1,
                getContentWidth(), 1f, borderColor);
        }
    }
}