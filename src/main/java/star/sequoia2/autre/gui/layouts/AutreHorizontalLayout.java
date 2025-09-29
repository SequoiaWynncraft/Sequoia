package star.sequoia2.autre.gui.layouts;

import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.containers.AutreContainer;

import java.util.List;

/**
 * Horizontal layout for arranging components from left to right
 */
public class AutreHorizontalLayout extends AutreLayout {
    
    public AutreHorizontalLayout(AutreContainer container) {
        super(container);
    }
    
    @Override
    public void layoutComponents() {
        List<AutreComponent> children = container.getChildren();
        if (children.isEmpty()) return;
        
        float contentHeight = container.getContentHeight();
        float currentX = container.getContentX();
        
        for (AutreComponent child : children) {
            if (!child.isVisible()) continue;
            
            child.setPosition(currentX, container.getContentY());
            
            // Use the child's current width or a default
            float childWidth = child.getWidth();
            if (childWidth <= 0) {
                childWidth = 100f; // Default width
            }
            
            // Set height to fill container height
            child.setSize(childWidth, contentHeight);
            
            currentX += childWidth + spacing;
        }
    }
}