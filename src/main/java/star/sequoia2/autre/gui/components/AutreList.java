package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * List component for displaying scrollable items
 */
public class AutreList extends AutreComponent {
    public static class ListItem {
        public final String title;
        public final String subtitle;
        public final String icon;
        public final EventHandler<MouseClickEvent> onClick;
        public boolean selected = false;
        
        public ListItem(String title, String subtitle, EventHandler<MouseClickEvent> onClick) {
            this(title, subtitle, null, onClick);
        }
        
        public ListItem(String title, String subtitle, String icon, EventHandler<MouseClickEvent> onClick) {
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
            this.onClick = onClick;
        }
    }
    
    protected java.util.List<ListItem> items = new java.util.ArrayList<>();
    protected java.util.List<ListItem> filteredItems = new java.util.ArrayList<>();
    protected int selectedIndex = -1;
    protected float itemHeight = 50f;
    protected float scrollOffset = 0f;
    protected String searchFilter = "";
    
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color itemBackgroundColor;
    protected AutreRenderer2.Color selectedColor;
    protected AutreRenderer2.Color hoverColor;
    protected AutreRenderer2.Color textColor;
    protected AutreRenderer2.Color subtitleColor;
    
    public AutreList(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND;
        this.itemBackgroundColor = AutreRenderer2.Color.SURFACE;
        this.selectedColor = AutreRenderer2.Color.getAccent();
        this.hoverColor = AutreRenderer2.Color.SURFACE.lighter(0.1f);
        this.textColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.subtitleColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.3f);
        
        // Add event handler
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                onMouseClick(event);
            }
        });
    }
    
    public AutreList addItem(String title, String subtitle, EventHandler<MouseClickEvent> onClick) {
        return addItem(title, subtitle, null, onClick);
    }
    
    public AutreList addItem(String title, String subtitle, String icon, EventHandler<MouseClickEvent> onClick) {
        items.add(new ListItem(title, subtitle, icon, onClick));
        updateFilteredItems();
        return this;
    }
    
    public AutreList setSearchFilter(String filter) {
        this.searchFilter = filter.toLowerCase();
        updateFilteredItems();
        return this;
    }
    
    public AutreList setItemHeight(float height) {
        this.itemHeight = height;
        return this;
    }
    
    public AutreList setSelectedIndex(int index) {
        if (selectedIndex >= 0 && selectedIndex < filteredItems.size()) {
            filteredItems.get(selectedIndex).selected = false;
        }
        this.selectedIndex = index;
        if (selectedIndex >= 0 && selectedIndex < filteredItems.size()) {
            filteredItems.get(selectedIndex).selected = true;
        }
        return this;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public void clearItems() {
        items.clear();
        filteredItems.clear();
        selectedIndex = -1;
    }
    
    private void updateFilteredItems() {
        filteredItems.clear();
        for (ListItem item : items) {
            if (searchFilter.isEmpty() || 
                item.title.toLowerCase().contains(searchFilter) || 
                (item.subtitle != null && item.subtitle.toLowerCase().contains(searchFilter))) {
                filteredItems.add(item);
            }
        }
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled || !event.isPressed()) return;
        
        float relativeY = event.y - getAbsoluteY() + scrollOffset;
        int clickedIndex = (int) (relativeY / itemHeight);
        
        if (clickedIndex >= 0 && clickedIndex < filteredItems.size()) {
            setSelectedIndex(clickedIndex);
            ListItem item = filteredItems.get(clickedIndex);
            if (item.onClick != null) {
                item.onClick.handle(event);
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Render background
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        
        // Enable scissor for scrolling
        AutreRenderer2.enableScissor((int) getAbsoluteX(), (int) getAbsoluteY(), 
            (int) width, (int) height);
        
        // Render items
        float itemY = getAbsoluteY() - scrollOffset;
        for (int i = 0; i < filteredItems.size(); i++) {
            ListItem item = filteredItems.get(i);
            
            // Skip items outside visible area
            if (itemY + itemHeight < getAbsoluteY() || itemY > getAbsoluteY() + height) {
                itemY += itemHeight;
                continue;
            }
            
            // Determine colors
            AutreRenderer2.Color itemBgColor = itemBackgroundColor;
            if (item.selected) {
                itemBgColor = selectedColor;
            } else if (contains(mouseX, itemY + itemHeight / 2)) {
                itemBgColor = hoverColor;
            }
            
            // Render item background
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), itemY, width, itemHeight, itemBgColor);
            
            // Render icon
            float contentX = getAbsoluteX() + 15;
            if (item.icon != null && !item.icon.isEmpty()) {
                AutreRenderer2.Color renderIconColor = item.selected ? 
                    AutreRenderer2.Color.WHITE : textColor;
                
                context.drawText(mc.textRenderer, item.icon, 
                    (int) contentX, (int) (itemY + itemHeight / 2 - 8), 
                    renderIconColor.toRGB(), false);
                contentX += 30;
            }
            
            // Render title
            if (item.title != null && !item.title.isEmpty()) {
                AutreRenderer2.Color renderTextColor = item.selected ? 
                    AutreRenderer2.Color.WHITE : textColor;
                
                context.drawText(mc.textRenderer, item.title, 
                    (int) contentX, (int) (itemY + itemHeight / 2 - 12), 
                    renderTextColor.toRGB(), false);
            }
            
            // Render subtitle
            if (item.subtitle != null && !item.subtitle.isEmpty()) {
                AutreRenderer2.Color renderSubtitleColor = item.selected ? 
                    AutreRenderer2.Color.WHITE.darker(0.2f) : subtitleColor;
                
                context.drawText(mc.textRenderer, item.subtitle, 
                    (int) contentX, (int) (itemY + itemHeight / 2 + 2), 
                    renderSubtitleColor.toRGB(), false);
            }
            
            // No separators - clean flat design
            
            itemY += itemHeight;
        }
        
        AutreRenderer2.disableScissor();
        
        // Render scrollbar if needed
        float totalContentHeight = filteredItems.size() * itemHeight;
        if (totalContentHeight > height) {
            float scrollbarHeight = (height / totalContentHeight) * height;
            float scrollbarY = getAbsoluteY() + (scrollOffset / totalContentHeight) * height;
            
            AutreRenderer2.Color scrollbarColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX() + width - 4, scrollbarY, 4, scrollbarHeight, scrollbarColor);
        }
        
        // No borders - clean flat design
    }
}