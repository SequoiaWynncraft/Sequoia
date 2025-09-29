package star.sequoia2.autre.gui.layouts;

import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.containers.AutreContainer;

import java.util.List;

/**
 * Grid layout for arranging components in rows and columns
 */
public class AutreGridLayout extends AutreLayout {
    protected int rows;
    protected int columns;
    
    public AutreGridLayout(AutreContainer container, int rows, int columns) {
        super(container);
        this.rows = rows;
        this.columns = columns;
    }
    
    public AutreGridLayout setGrid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        layoutComponents();
        return this;
    }
    
    @Override
    public void layoutComponents() {
        List<AutreComponent> children = container.getChildren();
        if (children.isEmpty()) return;
        
        float contentWidth = container.getContentWidth();
        float contentHeight = container.getContentHeight();
        
        float cellWidth = (contentWidth - (spacing * (columns - 1))) / columns;
        float cellHeight = (contentHeight - (spacing * (rows - 1))) / rows;
        
        int index = 0;
        for (int row = 0; row < rows && index < children.size(); row++) {
            for (int col = 0; col < columns && index < children.size(); col++) {
                AutreComponent child = children.get(index);
                
                float x = container.getContentX() + col * (cellWidth + spacing);
                float y = container.getContentY() + row * (cellHeight + spacing);
                
                child.setPosition(x, y);
                child.setSize(cellWidth, cellHeight);
                
                index++;
            }
        }
    }
}