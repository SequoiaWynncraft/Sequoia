package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.ArrayList;
import java.util.List;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Flat paginator component matching the keyboard settings style
 */
public class AutrePaginator extends AutreComponent {
    protected List<String> pageNames;
    protected int currentPage = 0;
    protected Runnable onPageChange;
    
    // Visual properties for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.BACKGROUND;
    protected AutreRenderer2.Color activeColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color inactiveColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color activeTextColor = AutreRenderer2.Color.TEXT_PRIMARY;
    
    protected float tabHeight = 30f;
    protected float tabSpacing = 2f;
    
    public AutrePaginator(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.pageNames = new ArrayList<>();
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutrePaginator addPage(String name) {
        pageNames.add(name);
        return this;
    }
    
    public AutrePaginator setPages(List<String> pages) {
        this.pageNames = new ArrayList<>(pages);
        return this;
    }
    
    public AutrePaginator setCurrentPage(int page) {
        if (page >= 0 && page < pageNames.size()) {
            this.currentPage = page;
            if (onPageChange != null) {
                onPageChange.run();
            }
        }
        return this;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public String getCurrentPageName() {
        return pageNames.isEmpty() ? "" : pageNames.get(currentPage);
    }
    
    public AutrePaginator setOnPageChange(Runnable callback) {
        this.onPageChange = callback;
        return this;
    }
    
    public AutrePaginator setActiveColor(AutreRenderer2.Color color) {
        this.activeColor = color;
        return this;
    }
    
    public AutrePaginator setInactiveColor(AutreRenderer2.Color color) {
        this.inactiveColor = color;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float tabWidth = (width - (tabSpacing * (pageNames.size() - 1))) / pageNames.size();
        
        for (int i = 0; i < pageNames.size(); i++) {
            float tabX = getAbsoluteX() + i * (tabWidth + tabSpacing);
            
            if (event.x >= tabX && event.x <= tabX + tabWidth &&
                event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + tabHeight) {
                setCurrentPage(i);
                break;
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible || pageNames.isEmpty()) return;
        
        float tabWidth = (width - (tabSpacing * (pageNames.size() - 1))) / pageNames.size();
        
        for (int i = 0; i < pageNames.size(); i++) {
            float tabX = getAbsoluteX() + i * (tabWidth + tabSpacing);
            boolean isActive = i == currentPage;
            boolean isHovered = enabled && 
                               mouseX >= tabX && mouseX <= tabX + tabWidth &&
                               mouseY >= getAbsoluteY() && mouseY <= getAbsoluteY() + tabHeight;
            
            // Tab background - flat rectangles
            AutreRenderer2.Color bgColor;
            AutreRenderer2.Color textCol;
            
            if (isActive) {
                bgColor = activeColor;
                textCol = activeTextColor;
            } else if (isHovered) {
                bgColor = inactiveColor.lighter(0.1f);
                textCol = textColor.lighter(0.1f);
            } else {
                bgColor = inactiveColor;
                textCol = textColor;
            }
            
            // Render tab background
            AutreRenderer2.fillRect(context.getMatrices(),
                tabX, getAbsoluteY(), tabWidth, tabHeight, bgColor);
            
            // Render tab text
            String tabName = pageNames.get(i);
            if (tabName != null) {
                int textWidth = mc.textRenderer.getWidth(tabName);
                int textHeight = mc.textRenderer.fontHeight;
                
                float textX = tabX + (tabWidth - textWidth) / 2;
                float textY = getAbsoluteY() + (tabHeight - textHeight) / 2;
                
                AutreRenderer2.drawText(context, tabName, textX, textY, textCol, false);
            }
        }
    }
}