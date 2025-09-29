package star.sequoia2.autre.gui.layouts;

import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.containers.AutreContainer;

import java.util.List;

/**
 * Vertical layout for stacking components from top to bottom
 */
public class AutreVerticalLayout extends AutreLayout {
    
    public AutreVerticalLayout(AutreContainer container) {
        super(container);
    }
    
    @Override
    public void layoutComponents() {
        List<AutreComponent> children = container.getChildren();
        if (children.isEmpty()) return;
        
        float contentWidth = container.getContentWidth();
        float currentY = container.getContentY();
        
        for (AutreComponent child : children) {
            if (!child.isVisible()) continue;
            
            child.setPosition(container.getContentX(), currentY);
            
            // Use the child's current height or a default
            float childHeight = child.getHeight();
            if (childHeight <= 0) {
                childHeight = 30f; // Default height
            }
            
            // Set width to fill container width
            child.setSize(contentWidth, childHeight);
            
            currentY += childHeight + spacing;
        }
    }
}