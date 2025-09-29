package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Side navigation menu component
 */
public class AutreNavigationMenu extends AutreComponent {
    public static class NavigationItem {
        public final String text;
        public final String icon; // Unicode or character
        public final EventHandler<MouseClickEvent> onClick;
        public boolean selected = false;
        
        public NavigationItem(String icon, String text, EventHandler<MouseClickEvent> onClick) {
            this.icon = icon;
            this.text = text;
            this.onClick = onClick;
        }
    }
    
    protected java.util.List<NavigationItem> items = new java.util.ArrayList<>();
    protected int selectedIndex = -1;
    protected float itemHeight = 40f;
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color selectedColor;
    protected AutreRenderer2.Color hoverColor;
    protected AutreRenderer2.Color textColor;
    protected AutreRenderer2.Color iconColor;
    
    public AutreNavigationMenu(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND.darker(0.2f);
        this.selectedColor = AutreRenderer2.Color.getAccent();
        this.hoverColor = AutreRenderer2.Color.SURFACE;
        this.textColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.iconColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.2f);
        
        // Add event handler
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                onMouseClick(event);
            }
        });
    }
    
    public AutreNavigationMenu addItem(String icon, String text, EventHandler<MouseClickEvent> onClick) {
        items.add(new NavigationItem(icon, text, onClick));
        return this;
    }
    
    public AutreNavigationMenu setSelectedIndex(int index) {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            items.get(selectedIndex).selected = false;
        }
        this.selectedIndex = index;
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            items.get(selectedIndex).selected = true;
        }
        return this;
    }
    
    public AutreNavigationMenu setItemHeight(float height) {
        this.itemHeight = height;
        return this;
    }
    
    public AutreNavigationMenu setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public AutreNavigationMenu setSelectedColor(AutreRenderer2.Color color) {
        this.selectedColor = color;
        return this;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled || !event.isPressed()) return;
        
        float itemY = getAbsoluteY() + 15f;
        for (int i = 0; i < items.size(); i++) {
            NavigationItem item = items.get(i);
            
            // Fixed hitbox calculation to match visual rendering
            if (event.x >= getAbsoluteX() && event.x <= getAbsoluteX() + width &&
                event.y >= itemY - 3f && event.y <= itemY + itemHeight + 3f) {
                
                // Deselect current item
                if (selectedIndex >= 0 && selectedIndex < items.size()) {
                    items.get(selectedIndex).selected = false;
                }
                
                // Select new item
                selectedIndex = i;
                item.selected = true;
                
                // Call click handler
                if (item.onClick != null) {
                    item.onClick.handle(event);
                }
                break;
            }
            
            itemY += itemHeight + 5f;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Render background
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        
                // Render menu items
        float itemY = getAbsoluteY() + 15f;
        for (int i = 0; i < items.size(); i++) {
            NavigationItem item = items.get(i);
            
            // Check if mouse is hovering over this item
            boolean isHovered = mouseX >= getAbsoluteX() && mouseX <= getAbsoluteX() + width &&
                               mouseY >= itemY - 3f && mouseY <= itemY + itemHeight + 3f;
            
            // Colors based on selection, hover, and enabled state
            AutreRenderer2.Color bgColor;
            AutreRenderer2.Color iconCol;
            AutreRenderer2.Color textCol;
            
            if (item.selected) {
                bgColor = selectedColor;
                iconCol = AutreRenderer2.Color.BACKGROUND;
                textCol = AutreRenderer2.Color.BACKGROUND;
            } else if (isHovered) {
                bgColor = hoverColor;
                iconCol = iconColor.lighter(0.2f);
                textCol = textColor.lighter(0.2f);
            } else {
                bgColor = backgroundColor;
                iconCol = iconColor;
                textCol = textColor;
            }
            
            // Render item background for selected/hovered items
            if (item.selected || isHovered) {
                AutreRenderer2.fillRect(context.getMatrices(),
                    getAbsoluteX(), itemY - 3f, width, itemHeight + 6f, bgColor);
            }
            
            // Render icon with better positioning
            if (item.icon != null && !item.icon.isEmpty()) {
                context.drawText(mc.textRenderer, item.icon,
                    (int) (getAbsoluteX() + 8f), (int) (itemY + 1f),
                    iconCol.toRGB(), false);
            }
            
            // Render text with proper alignment
            context.drawText(mc.textRenderer, item.text,
                (int) (getAbsoluteX() + 25f), (int) (itemY + 1f),
                textCol.toRGB(), false);
            
            itemY += itemHeight + 5f;
        }
        
        // No borders - clean flat design
    }
}