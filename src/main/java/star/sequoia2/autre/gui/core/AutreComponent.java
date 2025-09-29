package star.sequoia2.autre.gui.core;

import net.minecraft.client.util.math.MatrixStack;
import star.sequoia2.autre.gui.events.GuiClickEvent;
import star.sequoia2.autre.gui.events.GuiHoverEvent;

/**
 * Base class for all GUI components in the Autre GUI system
 */
public abstract class AutreComponent {
    protected float x, y, width, height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean hovered = false;
    
    public AutreComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // Position and size
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    
    // Visibility and state
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isHovered() { return hovered; }
    
    // Hit testing
    public boolean contains(float mouseX, float mouseY) {
        return visible && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    // Event handling
    public void onMouseMove(float mouseX, float mouseY) {
        boolean wasHovered = hovered;
        hovered = contains(mouseX, mouseY);
        
        if (hovered != wasHovered) {
            onHover(new GuiHoverEvent(mouseX, mouseY, hovered));
        }
    }
    
    public void onMouseClick(GuiClickEvent event) {
        if (!enabled || !visible) return;
        
        if (contains(event.getMouseX(), event.getMouseY())) {
            onClick(event);
        }
    }
    
    // Abstract methods that components must implement
    public abstract void render(MatrixStack matrices, float mouseX, float mouseY, float delta);
    
    // Event handlers that can be overridden
    protected void onClick(GuiClickEvent event) {}
    protected void onHover(GuiHoverEvent event) {}
    
    // Update method for animations and state changes
    public void update(float delta) {}
}