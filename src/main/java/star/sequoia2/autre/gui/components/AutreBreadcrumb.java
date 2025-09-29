package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.*;
import java.util.function.Consumer;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Breadcrumb navigation component with flat design
 */
public class AutreBreadcrumb extends AutreComponent {
    protected List<BreadcrumbItem> items = new ArrayList<>();
    protected float itemSpacing = 8f;
    protected float separatorWidth = 6f;
    protected String separatorText = ">";
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.TRANSPARENT;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.3f);
    protected AutreRenderer2.Color activeTextColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color hoverTextColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color separatorColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.6f);
    
    protected Consumer<String> onItemClick;
    
    public AutreBreadcrumb(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreBreadcrumb setItems(String... items) {
        this.items.clear();
        for (String item : items) {
            this.items.add(new BreadcrumbItem(item, item));
        }
        return this;
    }
    
    public AutreBreadcrumb addItem(String id, String label) {
        this.items.add(new BreadcrumbItem(id, label));
        return this;
    }
    
    public AutreBreadcrumb removeItem(String id) {
        items.removeIf(item -> item.id.equals(id));
        return this;
    }
    
    public AutreBreadcrumb clearItems() {
        items.clear();
        return this;
    }
    
    public AutreBreadcrumb setOnItemClick(Consumer<String> callback) {
        this.onItemClick = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float currentX = getAbsoluteX();
        float mouseX = event.x;
        
        for (int i = 0; i < items.size(); i++) {
            BreadcrumbItem item = items.get(i);
            float itemWidth = mc.textRenderer.getWidth(item.label);

            if (mouseX >= currentX && mouseX <= currentX + itemWidth) {
                // Clicked on item
                if (onItemClick != null) {
                    onItemClick.accept(item.id);
                }
                break;
            }
            
            currentX += itemWidth + itemSpacing;
            
            // Add separator width if not last item
            if (i < items.size() - 1) {
                currentX += separatorWidth + itemSpacing;
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible || items.isEmpty()) return;
        
        // Background (optional)
        if (backgroundColor != AutreRenderer2.Color.TRANSPARENT) {
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        }
        
        // Create base text style
        star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle baseStyle = 
            star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle.builder()
                .size(12f)
                .build();
                
        float textHeight = star.sequoia2.autre.render.text.AutreTextRenderer.getTextHeight(baseStyle);
        float currentX = getAbsoluteX();
        float textY = getAbsoluteY() + (height - textHeight) / 2f;
        
        for (int i = 0; i < items.size(); i++) {
            BreadcrumbItem item = items.get(i);
            
            // Create text style based on item state
            star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle.Builder styleBuilder = 
                star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle.builder()
                    .size(12f);
            
            // Determine text color and styling
            AutreRenderer2.Color currentTextColor;
            boolean isLast = (i == items.size() - 1);
            boolean isHovered = enabled && mouseX >= currentX && mouseY >= getAbsoluteY() && 
                              mouseY <= getAbsoluteY() + height;
            
            if (isLast) {
                // Last item (current page) - not clickable
                currentTextColor = activeTextColor;
                styleBuilder.bold(true);
            } else if (isHovered) {
                // Hovering over clickable item
                currentTextColor = hoverTextColor;
                styleBuilder.underline(true);
            } else {
                // Regular clickable item
                currentTextColor = textColor;
            }
            
            star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle itemStyle = 
                styleBuilder.color(currentTextColor).build();
                
            float itemWidth = star.sequoia2.autre.render.text.AutreTextRenderer.getTextWidth(item.label, itemStyle);
            
            // Render item text
            AutreRenderer2.drawText(context, item.label, currentX, textY, itemStyle);
            
            currentX += itemWidth + itemSpacing;
            
            // Render separator if not last item
            if (i < items.size() - 1) {
                star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle separatorStyle = 
                    star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle.builder()
                        .color(separatorColor)
                        .size(10f)
                        .build();
                        
                AutreRenderer2.drawText(context, separatorText, currentX, textY, separatorStyle);
                currentX += separatorWidth + itemSpacing;
            }
        }
    }
    
    // Helper class for breadcrumb items
    protected static class BreadcrumbItem {
        public final String id;
        public final String label;
        
        public BreadcrumbItem(String id, String label) {
            this.id = id;
            this.label = label;
        }
    }
}