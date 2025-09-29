package star.sequoia2.autre.gui.layouts;

import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.containers.AutreContainer;

/**
 * Base layout class for organizing components within containers
 */
public abstract class AutreLayout {
    protected AutreContainer container;
    protected float spacing = 4f;
    
    public AutreLayout(AutreContainer container) {
        this.container = container;
    }
    
    public AutreLayout setSpacing(float spacing) {
        this.spacing = spacing;
        return this;
    }
    
    public float getSpacing() {
        return spacing;
    }
    
    /**
     * Layout all components within the container
     */
    public abstract void layoutComponents();
    
    /**
     * Add a component to the layout
     */
    public void addComponent(AutreComponent component) {
        container.addChild(component);
        layoutComponents();
    }
    
    /**
     * Remove a component from the layout
     */
    public void removeComponent(AutreComponent component) {
        container.removeChild(component);
        layoutComponents();
    }
}