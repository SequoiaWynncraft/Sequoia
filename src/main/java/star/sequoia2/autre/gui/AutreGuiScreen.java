package star.sequoia2.autre.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.ArrayList;
import java.util.List;

/**
 * AutreGUI - Full rebuild mode
 * This mode rebuilds the entire UI every frame for maximum flexibility
 */
public class AutreGuiScreen extends Screen {
    protected final List<AutreComponent> rootComponents = new ArrayList<>();
    protected AutreComponent focusedComponent;
    protected boolean backgroundBlur = false;
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.BACKGROUND;
    
    // Position and size - can be smaller than full screen
    protected float guiX, guiY;
    protected float guiWidth, guiHeight;
    protected boolean resizable = false;
    protected boolean draggable = false;
    
    // Dragging state
    protected boolean dragging = false;
    protected float dragOffsetX, dragOffsetY;
    
    public AutreGuiScreen(String title, float x, float y, float width, float height) {
        super(Text.literal(title));
        this.guiX = x;
        this.guiY = y;
        this.guiWidth = width;
        this.guiHeight = height;
    }
    
    public AutreGuiScreen(String title) {
        this(title, 0, 0, 0, 0);
        // Full screen mode
        this.guiWidth = -1;
        this.guiHeight = -1;
    }
    
    // Configuration
    public AutreGuiScreen setBackgroundBlur(boolean blur) {
        this.backgroundBlur = blur;
        return this;
    }
    
    public AutreGuiScreen setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public AutreGuiScreen setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }
    
    public AutreGuiScreen setDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }
    
    // Component management
    public void addComponent(AutreComponent component) {
        rootComponents.add(component);
    }
    
    public void removeComponent(AutreComponent component) {
        rootComponents.remove(component);
        if (focusedComponent == component) {
            focusedComponent = null;
        }
    }
    
    public void removeAllComponents() {
        rootComponents.clear();
        focusedComponent = null;
    }
    
    // Position and size management
    public void setGuiPosition(float x, float y) {
        this.guiX = x;
        this.guiY = y;
    }
    
    public void setGuiSize(float width, float height) {
        this.guiWidth = width;
        this.guiHeight = height;
    }
    
    public float getGuiX() { return guiX; }
    public float getGuiY() { return guiY; }
    public float getGuiWidth() { return guiWidth > 0 ? guiWidth : width; }
    public float getGuiHeight() { return guiHeight > 0 ? guiHeight : height; }
    
    @Override
    protected void init() {
        super.init();
        
        // If size is not set, use full screen
        if (guiWidth <= 0) guiWidth = width;
        if (guiHeight <= 0) guiHeight = height;
        
        // Center if position is not set
        if (guiX == 0 && guiY == 0 && (guiWidth < width || guiHeight < height)) {
            guiX = (width - guiWidth) / 2;
            guiY = (height - guiHeight) / 2;
        }
    }
    
    @Override
    public void resize(net.minecraft.client.MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        
        // Recalculate position to keep GUI centered when window resizes
        if (guiWidth > 0 && guiHeight > 0) {
            // Keep GUI centered
            guiX = (width - guiWidth) / 2f;
            guiY = (height - guiHeight) / 2f;
            
            // Ensure GUI doesn't go off-screen
            if (guiX < 0) guiX = 10;
            if (guiY < 0) guiY = 10;
            if (guiX + guiWidth > width) guiX = width - guiWidth - 10;
            if (guiY + guiHeight > height) guiY = height - guiHeight - 10;
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Apply blur effect if enabled
        if (backgroundBlur) {
            // TODO: Implement blur shader
        }
        
        // Render GUI background (flat rectangle)
        if (backgroundColor.a > 0) {
            AutreRenderer2.fillRect(context.getMatrices(), 
                guiX, guiY, getGuiWidth(), getGuiHeight(), backgroundColor);
        }
        
        // Update and render all components
        context.getMatrices().push();
        context.getMatrices().translate(guiX, guiY, 0);
        
        for (AutreComponent component : rootComponents) {
            component.update(delta);
            component.render(context, mouseX - guiX, mouseY - guiY, delta);
        }
        
        context.getMatrices().pop();
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float relativeX = (float) mouseX - guiX;
        float relativeY = (float) mouseY - guiY;
        
        // Check if click is within GUI bounds
        if (mouseX >= guiX && mouseX < guiX + getGuiWidth() && 
            mouseY >= guiY && mouseY < guiY + getGuiHeight()) {
            
            // Handle dragging if enabled
            if (draggable && button == 0 && relativeY < 30) { // Title bar area
                dragging = true;
                dragOffsetX = relativeX;
                dragOffsetY = relativeY;
                return true;
            }
            
            // Pass to components
            for (int i = rootComponents.size() - 1; i >= 0; i--) {
                AutreComponent component = rootComponents.get(i);
                if (component.handleMouseClick(relativeX, relativeY, button, true)) {
                    setFocusedComponent(component);
                    return true;
                }
            }
        } else {
            // Click outside GUI
            setFocusedComponent(null);
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            dragging = false;
            return true;
        }
        
        float relativeX = (float) mouseX - guiX;
        float relativeY = (float) mouseY - guiY;
        
        // Pass to components
        for (int i = rootComponents.size() - 1; i >= 0; i--) {
            AutreComponent component = rootComponents.get(i);
            if (component.handleMouseClick(relativeX, relativeY, button, false)) {
                return true;
            }
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (dragging) {
            guiX = (float) mouseX - dragOffsetX;
            guiY = (float) mouseY - dragOffsetY;
            
            // Clamp to screen bounds
            guiX = Math.max(0, Math.min(guiX, width - getGuiWidth()));
            guiY = Math.max(0, Math.min(guiY, height - getGuiHeight()));
        }
        
        super.mouseMoved(mouseX, mouseY);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Pass to focused component first
        if (focusedComponent != null) {
            if (focusedComponent.handleKeyEvent(keyCode, scanCode, modifiers, true)) {
                return true;
            }
        }
        
        // Pass to all components
        for (AutreComponent component : rootComponents) {
            if (component.handleKeyEvent(keyCode, scanCode, modifiers, true)) {
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Pass to focused component first
        if (focusedComponent != null) {
            if (focusedComponent.handleKeyEvent(keyCode, scanCode, modifiers, false)) {
                return true;
            }
        }
        
        // Pass to all components
        for (AutreComponent component : rootComponents) {
            if (component.handleKeyEvent(keyCode, scanCode, modifiers, false)) {
                return true;
            }
        }
        
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    
    private void setFocusedComponent(AutreComponent component) {
        if (focusedComponent != null) {
            focusedComponent.setFocused(false);
        }
        focusedComponent = component;
        if (focusedComponent != null) {
            focusedComponent.setFocused(true);
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // No-op: disables vanilla world blur
    }
}