package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.components.AutreComponent;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;
import star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle;

import java.util.*;
import java.util.function.Consumer;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Accordion menu with collapsible sections and flat design
 */
public class AutreAccordionMenu extends AutreContainer {
    protected List<AccordionSection> sections = new ArrayList<>();
    protected boolean allowMultipleExpanded = true;
    protected float sectionHeaderHeight = 28f;
    protected float itemHeight = 24f;
    protected float animationSpeed = 6f;
    
    // Colors for flat design
    protected AutreRenderer2.Color sectionHeaderBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color sectionHeaderHover = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color sectionContentBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color itemHoverBg = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color selectedItemBg = AutreRenderer2.Color.getAccent().withAlpha(0.2f);
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color headerTextColor = AutreRenderer2.Color.TEXT_PRIMARY;
    
    protected Consumer<String> onItemSelected;
    protected String selectedItemId = null;
    
    public AutreAccordionMenu(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreAccordionMenu addSection(String id, String title, boolean expanded) {
        sections.add(new AccordionSection(id, title, expanded));
        return this;
    }
    
    public AutreAccordionMenu addItemToSection(String sectionId, String itemId, String label, String icon) {
        for (AccordionSection section : sections) {
            if (section.id.equals(sectionId)) {
                section.items.add(new AccordionItem(itemId, label, icon));
                break;
            }
        }
        return this;
    }
    
    public AutreAccordionMenu removeSection(String sectionId) {
        sections.removeIf(section -> section.id.equals(sectionId));
        return this;
    }
    
    public AutreAccordionMenu setAllowMultipleExpanded(boolean allowMultiple) {
        this.allowMultipleExpanded = allowMultiple;
        
        // If switching to single mode, collapse all but first expanded
        if (!allowMultiple) {
            boolean foundExpanded = false;
            for (AccordionSection section : sections) {
                if (section.expanded && !foundExpanded) {
                    foundExpanded = true;
                } else if (section.expanded) {
                    section.expanded = false;
                    section.targetHeight = 0f;
                }
            }
        }
        return this;
    }
    
    public AutreAccordionMenu setSectionExpanded(String sectionId, boolean expanded) {
        for (AccordionSection section : sections) {
            if (section.id.equals(sectionId)) {
                if (!allowMultipleExpanded && expanded) {
                    // Collapse all other sections
                    for (AccordionSection otherSection : sections) {
                        if (!otherSection.id.equals(sectionId)) {
                            otherSection.expanded = false;
                            otherSection.targetHeight = 0f;
                        }
                    }
                }
                
                section.expanded = expanded;
                section.targetHeight = expanded ? section.items.size() * itemHeight : 0f;
                break;
            }
        }
        return this;
    }
    
    public AutreAccordionMenu setSelectedItem(String itemId) {
        this.selectedItemId = itemId;
        return this;
    }
    
    public AutreAccordionMenu setOnItemSelected(Consumer<String> callback) {
        this.onItemSelected = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        float currentY = absY + padding;
        
        for (AccordionSection section : sections) {
            // Check section header click
            if (event.x >= absX && event.x <= absX + width &&
                event.y >= currentY && event.y <= currentY + sectionHeaderHeight) {
                
                setSectionExpanded(section.id, !section.expanded);
                return;
            }
            
            currentY += sectionHeaderHeight;
            
            // Check section items (if expanded)
            if (section.currentHeight > 0) {
                for (AccordionItem item : section.items) {
                    if (event.x >= absX && event.x <= absX + width &&
                        event.y >= currentY && event.y <= currentY + itemHeight) {
                        
                        selectedItemId = item.id;
                        if (onItemSelected != null) {
                            onItemSelected.accept(item.id);
                        }
                        return;
                    }
                    
                    currentY += itemHeight;
                    if (currentY - (absY + padding) >= section.currentHeight + sectionHeaderHeight) {
                        break; // Don't render items beyond current height
                    }
                }
            } else {
                currentY += section.currentHeight;
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Update animations
        for (AccordionSection section : sections) {
            if (Math.abs(section.currentHeight - section.targetHeight) > 0.5f) {
                float diff = section.targetHeight - section.currentHeight;
                section.currentHeight += diff * animationSpeed * deltaTime / 1000f;
            } else {
                section.currentHeight = section.targetHeight;
            }
        }
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Background
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, sectionContentBg);
        
        // Sections
        float currentY = absY + padding;
        
        for (AccordionSection section : sections) {
            renderSection(context, section, absX, currentY, mouseX, mouseY);
            currentY += sectionHeaderHeight + section.currentHeight;
        }
    }
    
    private void renderSection(DrawContext context, AccordionSection section, float x, float y, float mouseX, float mouseY) {
        // Section header
        boolean isHeaderHovered = mouseX >= x && mouseX <= x + width &&
                                mouseY >= y && mouseY <= y + sectionHeaderHeight;
        
        AutreRenderer2.Color headerBg = isHeaderHovered ? sectionHeaderHover : sectionHeaderBg;
        AutreRenderer2.fillRect(context.getMatrices(),
            x, y, width, sectionHeaderHeight, headerBg);
        
        // Header border
        AutreRenderer2.strokeRect(context.getMatrices(),
            x, y, width, sectionHeaderHeight, 1f, borderColor);
        
                // Expand/collapse icon
        String expandIcon = section.expanded ? "▼" : "▶";
        float iconX = x + 8f;
        
        // Use advanced text renderer
        TextStyle iconStyle = 
            TextStyle.builder()
                .color(textColor)
                .size(12f)
                .build();
                
        float iconHeight = star.sequoia2.autre.render.text.AutreTextRenderer.getTextHeight(iconStyle);
        float iconY = y + (sectionHeaderHeight - iconHeight) / 2f;
        
        AutreRenderer2.drawText(context, expandIcon, iconX, iconY, iconStyle);
        
        // Section title
        String title = section.title;
        TextStyle titleStyle = 
            TextStyle.builder()
                .color(textColor)
                .size(12f)
                .bold(true)
                .build();
                
        float titleX = iconX + star.sequoia2.autre.render.text.AutreTextRenderer.getTextWidth(expandIcon, iconStyle) + 4f;
        float titleY = y + (sectionHeaderHeight - iconHeight) / 2f;
        
        AutreRenderer2.drawText(context, title, titleX, titleY, titleStyle);
        
        // Section content (items)
        if (section.currentHeight > 0) {
            float contentY = y + sectionHeaderHeight;
            float itemY = contentY;
            
            // Content background
            AutreRenderer2.fillRect(context.getMatrices(),
                x, contentY, width, section.currentHeight, sectionContentBg);
            
            // Items
            for (AccordionItem item : section.items) {
                if (itemY - contentY >= section.currentHeight) {
                    break; // Don't render beyond current height
                }
                
                boolean isItemHovered = mouseX >= x && mouseX <= x + width &&
                                      mouseY >= itemY && mouseY <= itemY + itemHeight;
                boolean isSelected = item.id.equals(selectedItemId);
                
                // Item background
                if (isSelected) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        x, itemY, width, itemHeight, selectedItemBg);
                } else if (isItemHovered) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        x, itemY, width, itemHeight, itemHoverBg);
                }
                
                // Create text styles
                TextStyle itemIconStyle = 
                    TextStyle.builder()
                        .color(textColor)
                        .size(10f)
                        .build();
                        
                TextStyle labelStyle = 
                    TextStyle.builder()
                        .color(textColor)
                        .size(10f)
                        .build();
                
                float textHeight = star.sequoia2.autre.render.text.AutreTextRenderer.getTextHeight(labelStyle);
                
                // Item icon
                if (!item.icon.isEmpty()) {
                    float itemIconX = x + 24f;
                    float itemIconY = itemY + (itemHeight - textHeight) / 2f;
                    
                    AutreRenderer2.drawText(context, item.icon, itemIconX, itemIconY, itemIconStyle);
                }
                
                // Item label
                float labelX = x + (!item.icon.isEmpty() ? 40f : 24f);
                float labelY = itemY + (itemHeight - textHeight) / 2f;
                
                // Clip text if necessary
                float availableWidth = width - labelX + x - 8f;
                String clippedLabel = clipTextToWidth(item.label, availableWidth, labelStyle);
                
                AutreRenderer2.drawText(context, clippedLabel, labelX, labelY, labelStyle);
                
                itemY += itemHeight;
            }
            
            // Content border
            AutreRenderer2.strokeRect(context.getMatrices(),
                x, contentY, width, section.currentHeight, 1f, borderColor);
        }
    }
    
    private String clipTextToWidth(String text, float maxWidth, TextStyle style) {
        if (star.sequoia2.autre.render.text.AutreTextRenderer.getTextWidth(text, style) <= maxWidth) {
            return text;
        }
        
        // Binary search for maximum length that fits
        int left = 0, right = text.length();
        while (left < right) {
            int mid = (left + right + 1) / 2;
            String candidate = text.substring(0, mid) + "...";
            if (star.sequoia2.autre.render.text.AutreTextRenderer.getTextWidth(candidate, style) <= maxWidth) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        
        return left > 0 ? text.substring(0, left) + "..." : "...";
    }
    
    // Legacy method for compatibility
    private String clipTextToWidth(String text, float maxWidth) {
        TextStyle defaultStyle = 
            TextStyle.DEFAULT;
        return clipTextToWidth(text, maxWidth, defaultStyle);
    }
    
    // Helper classes
    protected static class AccordionSection {
        public final String id;
        public final String title;
        public boolean expanded;
        public final List<AccordionItem> items = new ArrayList<>();
        public float currentHeight = 0f;
        public float targetHeight = 0f;
        
        public AccordionSection(String id, String title, boolean expanded) {
            this.id = id;
            this.title = title;
            this.expanded = expanded;
        }
    }
    
    protected static class AccordionItem {
        public final String id;
        public final String label;
        public final String icon;
        
        public AccordionItem(String id, String label, String icon) {
            this.id = id;
            this.label = label;
            this.icon = icon != null ? icon : "";
        }
    }
}