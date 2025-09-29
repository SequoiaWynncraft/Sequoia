package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

import java.util.*;
import java.util.function.Consumer;

/**
 * Tooltip menu (context menu) with flat design - appears on hover or right-click
 */
public class AutreTooltipMenu extends AutreContainer {
    protected List<TooltipItem> items = new ArrayList<>();
    protected boolean isVisible = false;
    protected float itemHeight = 22f;
    protected float minWidth = 120f;
    protected float maxWidth = 200f;
    protected float calculatedWidth = minWidth;
    protected float calculatedHeight = 0f;
    
    // Positioning
    protected float targetX = 0f;
    protected float targetY = 0f;
    protected boolean positionAbove = false;
    protected boolean positionLeft = false;
    
    // Animation
    protected float fadeAlpha = 0f;
    protected float targetAlpha = 0f;
    protected float fadeSpeed = 8f;
    
    // Colors for flat design
    protected AutreRenderer2.Color menuBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color itemHoverBg = AutreRenderer2.Color.getAccent().withAlpha(0.2f);
    protected AutreRenderer2.Color separatorColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color disabledTextColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color shadowColor = AutreRenderer2.Color.BLACK.withAlpha(0.2f);
    
    protected Consumer<String> onItemSelected;
    
    public AutreTooltipMenu(float x, float y) {
        super(x, y, 0, 0); // Size will be calculated
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreTooltipMenu addItem(String id, String label, String icon, boolean enabled) {
        items.add(new TooltipItem(id, label, icon, enabled, false));
        recalculateSize();
        return this;
    }
    
    public AutreTooltipMenu addItem(String id, String label, String icon) {
        return addItem(id, label, icon, true);
    }
    
    public AutreTooltipMenu addItem(String id, String label) {
        return addItem(id, label, "", true);
    }
    
    public AutreTooltipMenu addSeparator() {
        items.add(new TooltipItem("", "", "", true, true));
        recalculateSize();
        return this;
    }
    
    public AutreTooltipMenu removeItem(String id) {
        items.removeIf(item -> item.id.equals(id));
        recalculateSize();
        return this;
    }
    
    public AutreTooltipMenu clearItems() {
        items.clear();
        recalculateSize();
        return this;
    }
    
    public AutreTooltipMenu setOnItemSelected(Consumer<String> callback) {
        this.onItemSelected = callback;
        return this;
    }
    
    public void showAt(float x, float y, float screenWidth, float screenHeight) {
        this.targetX = x;
        this.targetY = y;
        
        // Adjust position to stay on screen
        positionLeft = x + calculatedWidth > screenWidth;
        positionAbove = y + calculatedHeight > screenHeight;
        
        // Update actual position
        this.x = positionLeft ? x - calculatedWidth : x;
        this.y = positionAbove ? y - calculatedHeight : y;
        this.width = calculatedWidth;
        this.height = calculatedHeight;
        
        // Show with animation
        isVisible = true;
        targetAlpha = 1f;
    }
    
    public void hide() {
        targetAlpha = 0f;
        // isVisible will be set to false when fade completes
    }
    
    public boolean isShowing() {
        return isVisible;
    }
    
    private void recalculateSize() {
        if (items.isEmpty()) {
            calculatedWidth = minWidth;
            calculatedHeight = 0f;
            return;
        }
        
        // Calculate width based on content
        calculatedWidth = minWidth;
        for (TooltipItem item : items) {
            if (!item.isSeparator) {
                float itemWidth = 16f; // Left padding
                if (!item.icon.isEmpty()) {
                    itemWidth += mc.textRenderer.getWidth(item.icon) + 8f;
                }
                itemWidth += mc.textRenderer.getWidth(item.label) + 16f; // Right padding
                calculatedWidth = Math.max(calculatedWidth, Math.min(maxWidth, itemWidth));
            }
        }
        
        // Calculate height
        calculatedHeight = 4f; // Top/bottom padding
        for (TooltipItem item : items) {
            if (item.isSeparator) {
                calculatedHeight += 6f; // Separator height
            } else {
                calculatedHeight += itemHeight;
            }
        }
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!isVisible || !enabled || fadeAlpha < 0.5f) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        if (event.pressed) {
            // Check if click is within menu bounds
            if (event.x >= absX && event.x <= absX + width &&
                event.y >= absY && event.y <= absY + height) {
                
                // Find clicked item
                float currentY = absY + 2f; // Top padding
                
                for (TooltipItem item : items) {
                    if (item.isSeparator) {
                        currentY += 6f;
                        continue;
                    }
                    
                    if (event.y >= currentY && event.y <= currentY + itemHeight) {
                        if (item.enabled && !item.id.isEmpty()) {
                            hide();
                            if (onItemSelected != null) {
                                onItemSelected.accept(item.id);
                            }
                        }
                        return;
                    }
                    
                    currentY += itemHeight;
                }
            } else {
                // Click outside - hide menu
                hide();
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!isVisible && fadeAlpha <= 0f) return;
        
        // Update fade animation
        if (Math.abs(fadeAlpha - targetAlpha) > 0.01f) {
            float diff = targetAlpha - fadeAlpha;
            fadeAlpha += diff * fadeSpeed * deltaTime / 1000f;
        } else {
            fadeAlpha = targetAlpha;
        }
        
        // Hide when fade completes
        if (fadeAlpha <= 0f) {
            isVisible = false;
            return;
        }
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Shadow effect
        AutreRenderer2.fillRect(context.getMatrices(),
            absX + 2f, absY + 2f, width, height, shadowColor.withAlpha(shadowColor.a * fadeAlpha));
        
        // Menu background
        AutreRenderer2.Color currentBg = menuBg.withAlpha(menuBg.a * fadeAlpha);
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, currentBg);
        
        // Menu border
        AutreRenderer2.Color currentBorder = borderColor.withAlpha(borderColor.a * fadeAlpha);
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX, absY, width, height, 1f, currentBorder);
        
        // Menu items
        renderItems(context, absX, absY, mouseX, mouseY);
    }
    
    private void renderItems(DrawContext context, float x, float y, float mouseX, float mouseY) {
        float currentY = y + 2f; // Top padding
        
        for (TooltipItem item : items) {
            if (item.isSeparator) {
                // Render separator
                float separatorY = currentY + 2f;
                AutreRenderer2.Color currentSeparator = separatorColor.withAlpha(separatorColor.a * fadeAlpha);
                AutreRenderer2.fillRect(context.getMatrices(),
                    x + 8f, separatorY, width - 16f, 1f, currentSeparator);
                currentY += 6f;
                continue;
            }
            
            boolean isHovered = mouseX >= x && mouseX <= x + width &&
                              mouseY >= currentY && mouseY <= currentY + itemHeight;
            
            // Item background
            if (isHovered && item.enabled) {
                AutreRenderer2.Color currentHover = itemHoverBg.withAlpha(itemHoverBg.a * fadeAlpha);
                AutreRenderer2.fillRect(context.getMatrices(),
                    x, currentY, width, itemHeight, currentHover);
            }
            
            // Item content
            float contentX = x + 8f;
            float contentY = currentY + (itemHeight - mc.textRenderer.fontHeight) / 2f;
            
            // Item icon
            if (!item.icon.isEmpty()) {
                AutreRenderer2.Color currentIconColor = (item.enabled ? textColor : disabledTextColor)
                    .withAlpha((item.enabled ? textColor.a : disabledTextColor.a) * fadeAlpha);
                AutreRenderer2.drawText(context, item.icon, contentX, contentY, currentIconColor, false);
                contentX += mc.textRenderer.getWidth(item.icon) + 8f;
            }
            
            // Item label
            AutreRenderer2.Color currentTextColor = (item.enabled ? textColor : disabledTextColor)
                .withAlpha((item.enabled ? textColor.a : disabledTextColor.a) * fadeAlpha);
            
            // Clip text if necessary
            float availableWidth = x + width - contentX - 8f;
            String clippedLabel = clipTextToWidth(item.label, availableWidth);
            
            AutreRenderer2.drawText(context, clippedLabel, contentX, contentY, currentTextColor, false);
            
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
    
    // Helper class for tooltip items
    protected static class TooltipItem {
        public final String id;
        public final String label;
        public final String icon;
        public final boolean enabled;
        public final boolean isSeparator;
        
        public TooltipItem(String id, String label, String icon, boolean enabled, boolean isSeparator) {
            this.id = id;
            this.label = label;
            this.icon = icon;
            this.enabled = enabled;
            this.isSeparator = isSeparator;
        }
    }
}