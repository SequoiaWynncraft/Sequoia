package star.sequoia2.autre.gui;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.HashSet;
import java.util.Set;

/**
 * AutrePartialGUI - Partial update mode
 * This mode only updates and redraws components that have been marked as dirty
 * More efficient for complex UIs with many static elements
 */
public class AutrePartialGuiScreen extends AutreGuiScreen {
    protected final Set<AutreComponent> dirtyComponents = new HashSet<>();
    protected boolean fullRedrawNeeded = true;
    
    public AutrePartialGuiScreen(String title, float x, float y, float width, float height) {
        super(title, x, y, width, height);
    }
    
    public AutrePartialGuiScreen(String title) {
        super(title);
    }
    
    /**
     * Mark a component as dirty (needs redraw)
     */
    public void markDirty(AutreComponent component) {
        dirtyComponents.add(component);
    }
    
    /**
     * Mark the entire GUI for full redraw
     */
    public void markFullRedraw() {
        fullRedrawNeeded = true;
    }
    
    /**
     * Clear all dirty marks
     */
    public void clearDirtyMarks() {
        dirtyComponents.clear();
        fullRedrawNeeded = false;
    }
    
    @Override
    public void addComponent(AutreComponent component) {
        super.addComponent(component);
        markDirty(component);
    }
    
    @Override
    public void removeComponent(AutreComponent component) {
        super.removeComponent(component);
        dirtyComponents.remove(component);
        markFullRedraw(); // Need full redraw when removing components
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Check if full redraw is needed
        boolean needsFullRedraw = fullRedrawNeeded || 
                                 !dirtyComponents.isEmpty() || 
                                 isAnyComponentAnimated();
        
        if (needsFullRedraw) {
            // Apply blur effect if enabled
            if (backgroundBlur) {
                // TODO: Implement blur shader
            }
            
            // Render GUI background
            if (backgroundColor.a > 0) {
                AutreRenderer2.fillRoundedRect(context.getMatrices(), 
                    guiX, guiY, getGuiWidth(), getGuiHeight(), 12f, backgroundColor);
            }
            
            // Update and render components
            context.getMatrices().push();
            context.getMatrices().translate(guiX, guiY, 0);
            
            if (fullRedrawNeeded) {
                // Full update - all components
                for (AutreComponent component : rootComponents) {
                    component.update(delta);
                    component.render(context, mouseX - guiX, mouseY - guiY, delta);
                }
            } else {
                // Partial update - only dirty or animated components
                for (AutreComponent component : rootComponents) {
                    boolean shouldUpdate = dirtyComponents.contains(component) || 
                                         hasAnimatedChildren(component);
                    
                    if (shouldUpdate) {
                        component.update(delta);
                        component.render(context, mouseX - guiX, mouseY - guiY, delta);
                    } else {
                        // Still need to render static components but don't update them
                        component.render(context, mouseX - guiX, mouseY - guiY, 0);
                    }
                }
            }
            
            context.getMatrices().pop();
            clearDirtyMarks();
        }
        
        // Always call super for overlay elements
        super.render(context, mouseX, mouseY, delta);
    }
    
    /**
     * Check if any component is currently animated
     */
    private boolean isAnyComponentAnimated() {
        for (AutreComponent component : rootComponents) {
            if (isComponentAnimated(component)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a component or its children are animated
     */
    private boolean isComponentAnimated(AutreComponent component) {
        // For now, assume components with hover state changes are "animated"
        // In a full implementation, you'd track actual animation states
        if (component.isHovered()) {
            return true;
        }
        
        return hasAnimatedChildren(component);
    }
    
    /**
     * Check if component has animated children
     */
    private boolean hasAnimatedChildren(AutreComponent component) {
        for (AutreComponent child : component.getChildren()) {
            if (isComponentAnimated(child)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Mouse interactions typically require updates
        markFullRedraw();
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        
        // Check if any component hover states changed
        float relativeX = (float) mouseX - guiX;
        float relativeY = (float) mouseY - guiY;
        
        for (AutreComponent component : rootComponents) {
            AutreComponent hovered = component.findComponentAt(relativeX, relativeY);
            if (hovered != null) {
                markDirty(hovered);
            }
        }
    }
}