package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.*;

/**
 * Base class for all GUI components in the AutreGUI system
 */
public abstract class AutreComponent {
    protected float x, y, width, height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    
    // Event handlers
    protected final Map<Class<? extends AutreGuiEvent>, List<EventHandler<?>>> eventHandlers = new HashMap<>();
    
    // Child components
    protected final List<AutreComponent> children = new ArrayList<>();
    protected AutreComponent parent;
    
    // Animation support
    protected float animationTime = 0f;
    
    public AutreComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // Positioning and sizing
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    public void setBounds(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }
    
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    
    public float getAbsoluteX() {
        return parent != null ? parent.getAbsoluteX() + x : x;
    }
    
    public float getAbsoluteY() {
        return parent != null ? parent.getAbsoluteY() + y : y;
    }
    
    // State management
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isVisible() { return visible; }
    
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }
    
    public void setFocused(boolean focused) { this.focused = focused; }
    public boolean isFocused() { return focused; }
    
    public void setHovered(boolean hovered) { 
        if (this.hovered != hovered) {
            this.hovered = hovered;
            fireEvent(new MouseHoverEvent(0, 0, hovered));
        }
    }
    public boolean isHovered() { return hovered; }
    
    // Hit testing
    public boolean contains(float pointX, float pointY) {
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        return pointX >= absX && pointX < absX + width && 
               pointY >= absY && pointY < absY + height;
    }
    
    // Component hierarchy
    public void addChild(AutreComponent child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        child.parent = this;
        children.add(child);
    }
    
    public void removeChild(AutreComponent child) {
        if (children.remove(child)) {
            child.parent = null;
        }
    }
    
    public void removeAllChildren() {
        for (AutreComponent child : children) {
            child.parent = null;
        }
        children.clear();
    }
    
    public List<AutreComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    public AutreComponent getParent() {
        return parent;
    }
    
    // Event system
    @SuppressWarnings("unchecked")
    public <T extends AutreGuiEvent> void addEventListener(Class<T> eventType, EventHandler<T> handler) {
        eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add((EventHandler<AutreGuiEvent>) handler);
    }
    
    public <T extends AutreGuiEvent> void removeEventListener(Class<T> eventType, EventHandler<T> handler) {
        List<EventHandler<?>> handlers = eventHandlers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
            if (handlers.isEmpty()) {
                eventHandlers.remove(eventType);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void fireEvent(AutreGuiEvent event) {
        List<EventHandler<?>> handlers = eventHandlers.get(event.getClass());
        if (handlers != null) {
            for (EventHandler<?> handler : new ArrayList<>(handlers)) {
                ((EventHandler<AutreGuiEvent>) handler).handle(event);
                if (event.isCancelled()) break;
            }
        }
        
        // Bubble up to parent if not cancelled
        if (!event.isCancelled() && parent != null) {
            parent.fireEvent(event);
        }
    }
    
    // Convenience event handlers
    public void onClickStart(EventHandler<MouseClickEvent> handler) {
        addEventListener(MouseClickEvent.class, event -> {
            if (event.isPressed()) handler.handle(event);
        });
    }
    
    public void onClickRelease(EventHandler<MouseClickEvent> handler) {
        addEventListener(MouseClickEvent.class, event -> {
            if (event.isReleased()) handler.handle(event);
        });
    }
    
    public void onHover(EventHandler<MouseHoverEvent> handler) {
        addEventListener(MouseHoverEvent.class, handler);
    }
    
    public void onKeyPress(EventHandler<KeyEvent> handler) {
        addEventListener(KeyEvent.class, event -> {
            if (event.isPressed()) handler.handle(event);
        });
    }
    
    public void onKeyRelease(EventHandler<KeyEvent> handler) {
        addEventListener(KeyEvent.class, event -> {
            if (event.isReleased()) handler.handle(event);
        });
    }
    
    // Update and render methods
    public void update(float deltaTime) {
        if (!visible) return;
        
        animationTime += deltaTime;
        
        // Update children
        for (AutreComponent child : children) {
            child.update(deltaTime);
        }
    }
    
    public void render(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Check hover state
        boolean wasHovered = hovered;
        boolean nowHovered = contains(mouseX, mouseY);
        if (wasHovered != nowHovered) {
            setHovered(nowHovered);
        }
        
        // Render this component
        renderSelf(context, mouseX, mouseY, deltaTime);
        
        // Render children
        for (AutreComponent child : children) {
            child.render(context, mouseX, mouseY, deltaTime);
        }
    }
    
    protected abstract void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime);
    
    // Input handling
    public boolean handleMouseClick(float mouseX, float mouseY, int button, boolean pressed) {
        if (!visible || !enabled) return false;
        
        // Check children first (reverse order for correct Z-order)
        for (int i = children.size() - 1; i >= 0; i--) {
            AutreComponent child = children.get(i);
            if (child.handleMouseClick(mouseX, mouseY, button, pressed)) {
                return true;
            }
        }
        
        // Check if click is within this component
        if (contains(mouseX, mouseY)) {
            MouseClickEvent event = new MouseClickEvent(
                mouseX - getAbsoluteX(), 
                mouseY - getAbsoluteY(), 
                button, pressed
            );
            fireEvent(event);
            return !event.isCancelled();
        }
        
        return false;
    }
    
    public boolean handleKeyEvent(int keyCode, int scanCode, int modifiers, boolean pressed) {
        if (!visible || !enabled) return false;
        
        // Handle focused component first
        if (focused) {
            KeyEvent event = new KeyEvent(keyCode, scanCode, modifiers, pressed);
            fireEvent(event);
            if (event.isCancelled()) return true;
        }
        
        // Check children
        for (AutreComponent child : children) {
            if (child.handleKeyEvent(keyCode, scanCode, modifiers, pressed)) {
                return true;
            }
        }
        
        return false;
    }
    
    // Utility method to find component at position
    public AutreComponent findComponentAt(float x, float y) {
        if (!visible || !contains(x, y)) return null;
        
        // Check children first (reverse order for correct Z-order)
        for (int i = children.size() - 1; i >= 0; i--) {
            AutreComponent child = children.get(i);
            AutreComponent found = child.findComponentAt(x, y);
            if (found != null) return found;
        }
        
        return this;
    }
}