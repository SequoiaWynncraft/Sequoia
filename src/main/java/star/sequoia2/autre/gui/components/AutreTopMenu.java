package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Top menu bar component with action items
 */
public class AutreTopMenu extends AutreComponent {
    public static class MenuItem {
        public final String label;
        public final String value;
        public final EventHandler<MouseClickEvent> onClick;
        public boolean enabled = true;
        
        public MenuItem(String label, String value, EventHandler<MouseClickEvent> onClick) {
            this.label = label;
            this.value = value;
            this.onClick = onClick;
        }
    }
    
    protected java.util.List<MenuItem> items = new java.util.ArrayList<>();
    protected float itemWidth = 120f;
    protected float itemSpacing = 20f;
    
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color labelColor;
    protected AutreRenderer2.Color valueColor;
    protected AutreRenderer2.Color disabledColor;
    
    public AutreTopMenu(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        // Colors matching the mockups
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND.darker(0.2f);
        this.labelColor = AutreRenderer2.Color.getAccent();
        this.valueColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.disabledColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
        
        // Add event handler
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                onMouseClick(event);
            }
        });
    }
    
    public AutreTopMenu addItem(String label, String value, EventHandler<MouseClickEvent> onClick) {
        items.add(new MenuItem(label, value, onClick));
        return this;
    }
    
    public AutreTopMenu setItemEnabled(int index, boolean enabled) {
        if (index >= 0 && index < items.size()) {
            items.get(index).enabled = enabled;
        }
        return this;
    }
    
    public AutreTopMenu setItemWidth(float width) {
        this.itemWidth = width;
        return this;
    }
    
    public AutreTopMenu setItemSpacing(float spacing) {
        this.itemSpacing = spacing;
        return this;
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled || !event.isPressed()) return;
        
        float itemX = getAbsoluteX() + 20f;
        
        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            
            if (item.enabled && event.x >= itemX && event.x <= itemX + itemWidth &&
                event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height) {
                
                if (item.onClick != null) {
                    item.onClick.handle(event);
                }
                break;
            }
            
            itemX += itemWidth + itemSpacing;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Render background
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        
        // Render items
        float itemX = getAbsoluteX() + 15f;
        
        for (MenuItem item : items) {
            // Determine colors
            AutreRenderer2.Color lblColor = item.enabled ? labelColor : disabledColor;
            AutreRenderer2.Color valColor = item.enabled ? valueColor : disabledColor;
            
            // Render label (top, smaller)
            if (item.label != null && !item.label.isEmpty()) {
                context.drawText(mc.textRenderer, item.label,
                    (int) itemX, (int) (getAbsoluteY() + 5f),
                    lblColor.toRGB(), false);
            }
            
            // Render value (bottom, larger)
            if (item.value != null && !item.value.isEmpty()) {
                context.drawText(mc.textRenderer, item.value,
                    (int) itemX, (int) (getAbsoluteY() + 20f),
                    valColor.toRGB(), false);
            }
            
            itemX += itemWidth + itemSpacing;
        }
        
        // Render bottom border line
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY() + height - 1f, width, 1f,
            AutreRenderer2.Color.TEXT_PRIMARY.darker(0.6f));
    }
}