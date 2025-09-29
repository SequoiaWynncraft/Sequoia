package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Section header with tabs component
 */
public class AutreTabbedHeader extends AutreComponent {
    public static class Tab {
        public final String text;
        public final EventHandler<MouseClickEvent> onClick;
        public boolean selected = false;
        public boolean enabled = true;
        
        public Tab(String text, EventHandler<MouseClickEvent> onClick) {
            this.text = text;
            this.onClick = onClick;
        }
    }
    
    protected java.util.List<Tab> tabs = new java.util.ArrayList<>();
    protected String sectionTitle = "";
    protected int selectedIndex = -1;
    
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color titleColor;
    protected AutreRenderer2.Color tabBackgroundColor;
    protected AutreRenderer2.Color selectedTabColor;
    protected AutreRenderer2.Color textColor;
    protected AutreRenderer2.Color lineColor;
    
    public AutreTabbedHeader(float x, float y, float width, float height, String sectionTitle) {
        super(x, y, width, height);
        this.sectionTitle = sectionTitle;
        
        // Colors matching the mockups
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND;
        this.titleColor = AutreRenderer2.Color.getAccent();
        this.tabBackgroundColor = AutreRenderer2.Color.SURFACE;
        this.selectedTabColor = AutreRenderer2.Color.getAccent();
        this.textColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.lineColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.6f);
        
        // Add event handler
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                onMouseClick(event);
            }
        });
    }
    
    public AutreTabbedHeader addTab(String text, EventHandler<MouseClickEvent> onClick) {
        tabs.add(new Tab(text, onClick));
        return this;
    }
    
    public AutreTabbedHeader setSelectedIndex(int index) {
        if (selectedIndex >= 0 && selectedIndex < tabs.size()) {
            tabs.get(selectedIndex).selected = false;
        }
        this.selectedIndex = index;
        if (selectedIndex >= 0 && selectedIndex < tabs.size()) {
            tabs.get(selectedIndex).selected = true;
        }
        return this;
    }
    
    public AutreTabbedHeader setTabEnabled(int index, boolean enabled) {
        if (index >= 0 && index < tabs.size()) {
            tabs.get(index).enabled = enabled;
        }
        return this;
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled || !event.isPressed()) return;
        
        // Calculate tab area
        float titleWidth = sectionTitle != null ? mc.textRenderer.getWidth(sectionTitle) + 40f : 0f;
        float tabStartX = getAbsoluteX() + titleWidth;
        float tabY = getAbsoluteY() + 2f;
        
        // Check if click is within tab area
        float currentTabX = tabStartX;
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            float tabWidth = mc.textRenderer.getWidth(tab.text) + 16f;
            
            if (tab.enabled && event.x >= currentTabX && event.x <= currentTabX + tabWidth &&
                event.y >= tabY && event.y <= tabY + 20f) {
                
                // Deselect current tab
                if (selectedIndex >= 0 && selectedIndex < tabs.size()) {
                    tabs.get(selectedIndex).selected = false;
                }
                
                // Select new tab
                selectedIndex = i;
                tab.selected = true;
                
                // Call tab click handler
                if (tab.onClick != null) {
                    tab.onClick.handle(event);
                }
                break;
            }
            
            currentTabX += tabWidth + 2f;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Render section title in bold style
        if (sectionTitle != null && !sectionTitle.isEmpty()) {
            context.drawText(mc.textRenderer, sectionTitle,
                (int) (getAbsoluteX() + 5f), (int) (getAbsoluteY() + 5f),
                titleColor.toRGB(), false);
        }
        
        // Calculate tab area - tabs start after title with some spacing
        float titleWidth = sectionTitle != null ? mc.textRenderer.getWidth(sectionTitle) + 40f : 0f;
        float tabStartX = getAbsoluteX() + titleWidth;
        float tabY = getAbsoluteY() + 2f;
        
        // Render tabs with cleaner style
        float currentTabX = tabStartX;
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            float currentTabWidth = mc.textRenderer.getWidth(tab.text) + 16f;
            
            // Check if mouse is hovering over this tab
            boolean isHovered = tab.enabled && mouseX >= currentTabX && mouseX <= currentTabX + currentTabWidth &&
                               mouseY >= tabY && mouseY <= tabY + 20f;
            
            // Determine colors
            AutreRenderer2.Color bgColor;
            AutreRenderer2.Color txtColor;
            
            if (tab.selected) {
                bgColor = selectedTabColor;
                txtColor = AutreRenderer2.Color.BACKGROUND;
            } else if (isHovered && tab.enabled) {
                bgColor = AutreRenderer2.Color.SURFACE;
                txtColor = textColor.lighter(0.2f);
            } else {
                bgColor = AutreRenderer2.Color.SURFACE.darker(0.2f);
                txtColor = tab.enabled ? textColor : textColor.darker(0.5f);
            }
            
            // Render tab background with sharp edges
            AutreRenderer2.fillRect(context.getMatrices(),
                currentTabX, tabY, currentTabWidth, 20f, bgColor);
            
            // Render tab text
            context.drawText(mc.textRenderer, tab.text,
                (int) (currentTabX + 8f), (int) (tabY + 6f),
                txtColor.toRGB(), false);
            
            currentTabX += currentTabWidth + 2f;
        }
        
        // Render horizontal line across the full width
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY() + height - 1f, width, 1f, lineColor);
    }
}