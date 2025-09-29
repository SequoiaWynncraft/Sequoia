package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.*;
import java.util.function.Consumer;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Collapsible side menu with flat design and menu items
 */
public class AutreSideMenu extends AutreContainer {
    protected List<MenuItem> menuItems = new ArrayList<>();
    protected boolean isCollapsed = false;
    protected float expandedWidth;
    protected float collapsedWidth = 40f;
    protected float itemHeight = 32f;
    protected String selectedItemId = null;
    protected boolean showIcons = true;
    
    // Animation
    protected float currentWidth;
    protected float targetWidth;
    protected float animationSpeed = 8f;
    
    // Colors for flat design
    protected AutreRenderer2.Color menuBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color itemHoverBg = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color selectedItemBg = AutreRenderer2.Color.getAccent().withAlpha(0.2f);
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color iconColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.3f);
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    
    protected Consumer<String> onItemSelected;
    
    public AutreSideMenu(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.expandedWidth = width;
        this.currentWidth = width;
        this.targetWidth = width;
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreSideMenu addMenuItem(String id, String label, String icon) {
        menuItems.add(new MenuItem(id, label, icon));
        return this;
    }
    
    public AutreSideMenu addMenuSeparator() {
        menuItems.add(new MenuItem("", "", "", true));
        return this;
    }
    
    public AutreSideMenu removeMenuItem(String id) {
        menuItems.removeIf(item -> item.id.equals(id));
        return this;
    }
    
    public AutreSideMenu setSelectedItem(String id) {
        this.selectedItemId = id;
        return this;
    }
    
    public String getSelectedItem() {
        return selectedItemId;
    }
    
    public AutreSideMenu setCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
        this.targetWidth = collapsed ? collapsedWidth : expandedWidth;
        return this;
    }
    
    public boolean isCollapsed() {
        return isCollapsed;
    }
    
    public AutreSideMenu setOnItemSelected(Consumer<String> callback) {
        this.onItemSelected = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Check if click is within menu bounds
        if (event.x >= absX && event.x <= absX + currentWidth &&
            event.y >= absY && event.y <= absY + height) {
            
            // Check for collapse/expand button (top area)
            if (event.y <= absY + itemHeight) {
                setCollapsed(!isCollapsed);
                return;
            }
            
            // Check menu items
            float currentY = absY + itemHeight; // Start after header
            
            for (MenuItem item : menuItems) {
                if (item.isSeparator) {
                    currentY += itemHeight / 2f; // Separator takes half height
                    continue;
                }
                
                if (event.y >= currentY && event.y <= currentY + itemHeight) {
                    if (!item.id.isEmpty()) {
                        selectedItemId = item.id;
                        if (onItemSelected != null) {
                            onItemSelected.accept(item.id);
                        }
                    }
                    break;
                }
                
                currentY += itemHeight;
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Animate width
        if (Math.abs(currentWidth - targetWidth) > 0.5f) {
            float diff = targetWidth - currentWidth;
            currentWidth += diff * animationSpeed * deltaTime / 1000f;
        } else {
            currentWidth = targetWidth;
        }
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Menu background
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, currentWidth, height, menuBg);
        
        // Border
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX + currentWidth - 1f, absY, 1f, height, 1f, borderColor);
        
        // Header area with collapse button
        renderHeader(context, absX, absY, mouseX, mouseY);
        
        // Menu items
        renderMenuItems(context, absX, absY + itemHeight, mouseX, mouseY);
    }
    
    private void renderHeader(DrawContext context, float x, float y, float mouseX, float mouseY) {
        boolean isHeaderHovered = mouseX >= x && mouseX <= x + currentWidth &&
                                mouseY >= y && mouseY <= y + itemHeight;
        
        if (isHeaderHovered) {
            AutreRenderer2.fillRect(context.getMatrices(),
                x, y, currentWidth, itemHeight, itemHoverBg);
        }
        
        // Hamburger/collapse icon
        String collapseIcon = isCollapsed ? "☰" : "✕";
        float iconX = x + (currentWidth >= collapsedWidth ? 12f : (currentWidth - 16f) / 2f);
        float iconY = y + (itemHeight - mc.textRenderer.fontHeight) / 2f;
        
        AutreRenderer2.drawText(context, collapseIcon, iconX, iconY, iconColor, false);
        
        // Menu title (when expanded)
        if (!isCollapsed && currentWidth > collapsedWidth + 20f) {
            String title = "Menu";
            float titleX = x + 40f;
            AutreRenderer2.drawText(context, title, titleX, iconY, textColor, false);
        }
    }
    
    private void renderMenuItems(DrawContext context, float x, float y, float mouseX, float mouseY) {
        float currentY = y;
        
        for (MenuItem item : menuItems) {
            if (item.isSeparator) {
                // Render separator
                if (currentWidth > collapsedWidth) {
                    float separatorY = currentY + itemHeight / 4f;
                    AutreRenderer2.fillRect(context.getMatrices(),
                        x + 8f, separatorY, currentWidth - 16f, 1f, borderColor);
                }
                currentY += itemHeight / 2f;
                continue;
            }
            
            boolean isHovered = mouseX >= x && mouseX <= x + currentWidth &&
                              mouseY >= currentY && mouseY <= currentY + itemHeight;
            boolean isSelected = item.id.equals(selectedItemId);
            
            // Item background
            if (isSelected) {
                AutreRenderer2.fillRect(context.getMatrices(),
                    x, currentY, currentWidth, itemHeight, selectedItemBg);
            } else if (isHovered) {
                AutreRenderer2.fillRect(context.getMatrices(),
                    x, currentY, currentWidth, itemHeight, itemHoverBg);
            }
            
            // Item icon
            if (showIcons && !item.icon.isEmpty()) {
                float iconX = x + (currentWidth >= collapsedWidth ? 12f : (currentWidth - 16f) / 2f);
                float iconY = currentY + (itemHeight - mc.textRenderer.fontHeight) / 2f;
                
                AutreRenderer2.drawText(context, item.icon, iconX, iconY, iconColor, false);
            }
            
            // Item label (when expanded)
            if (!isCollapsed && currentWidth > collapsedWidth + 20f && !item.label.isEmpty()) {
                float labelX = x + 40f;
                float labelY = currentY + (itemHeight - mc.textRenderer.fontHeight) / 2f;
                
                // Clip text if necessary
                float availableWidth = currentWidth - 44f;
                String clippedLabel = clipTextToWidth(item.label, availableWidth);
                
                AutreRenderer2.drawText(context, clippedLabel, labelX, labelY, textColor, false);
            }
            
            currentY += itemHeight;
        }
    }
    
    private String clipTextToWidth(String text, float maxWidth) {
        if (mc.textRenderer.getWidth(text) <= maxWidth) {
            return text;
        }
        
        // Binary search for maximum length that fits
        int left = 0, right = text.length();
        while (left < right) {
            int mid = (left + right + 1) / 2;
            String candidate = text.substring(0, mid) + "...";
            if (mc.textRenderer.getWidth(candidate) <= maxWidth) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        
        return left > 0 ? text.substring(0, left) + "..." : "...";
    }
    
    @Override
    public float getContentX() {
        return x + (isCollapsed ? collapsedWidth : expandedWidth) + padding;
    }
    
    @Override
    public float getContentWidth() {
        return width - (isCollapsed ? collapsedWidth : expandedWidth) - (padding * 2);
    }
    
    // Helper class for menu items
    protected static class MenuItem {
        public final String id;
        public final String label;
        public final String icon;
        public final boolean isSeparator;
        
        public MenuItem(String id, String label, String icon) {
            this(id, label, icon, false);
        }
        
        public MenuItem(String id, String label, String icon, boolean isSeparator) {
            this.id = id;
            this.label = label;
            this.icon = icon;
            this.isSeparator = isSeparator;
        }
    }
}