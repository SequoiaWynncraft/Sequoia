package star.sequoia2.autre.gui.core;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI system with full rebuild capability
 * This version rebuilds the entire UI every frame
 */
public class AutreGUI {
    private final List<AutreComponent> components = new ArrayList<>();
    private float x, y, width, height;
    private boolean visible = false;
    private AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.BACKGROUND;
    private float cornerRadius = 8f;
    
    public AutreGUI(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // Component management
    public void addComponent(AutreComponent component) {
        components.add(component);
    }
    
    public void removeComponent(AutreComponent component) {
        components.remove(component);
    }
    
    public void clearComponents() {
        components.clear();
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
    
    // Styling
    public void setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
    }
    
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
    }
    
    // Visibility
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void show() { setVisible(true); }
    public void hide() { setVisible(false); }
    
    // Main render method - rebuilds everything
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        if (!visible) return;
        
        // Begin rendering
        AutreRenderer2.beginRender();
        
        // Render background
        AutreRenderer2.fillRoundedRect(context.getMatrices(), x, y, width, height, cornerRadius, backgroundColor);
        
        // Update and render all components
        for (AutreComponent component : components) {
            if (component.isVisible()) {
                component.update(delta);
                component.render(context, mouseX, mouseY, delta);
            }
        }
        
        // End rendering
        AutreRenderer2.endRender();
    }
    
    // Event handling
    public boolean handleClick(float mouseX, float mouseY, int button) {
        if (!visible) return false;
        
        // Check if click is within GUI bounds
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }
        
        // Process components in reverse order (top to bottom)
        for (int i = components.size() - 1; i >= 0; i--) {
            AutreComponent component = components.get(i);
            if (component.handleMouseClick(mouseX, mouseY, button, true)) {
                return true;
            }
        }
        
        return true; // Consume the event even if no component handled it
    }
    
    // Update method for animations
    public void update(float delta) {
        if (!visible) return;
        
        for (AutreComponent component : components) {
            component.update(delta);
        }
    }
}