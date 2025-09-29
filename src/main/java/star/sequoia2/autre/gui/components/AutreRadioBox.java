package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Radio button component for single selection within a group
 */
public class AutreRadioBox extends AutreComponent {
    protected String label;
    protected boolean selected = false;
    protected AutreRadioGroup group;
    protected Object value; // Associated value
    
    protected float radioSize = 16f;
    protected float labelSpacing = 8f;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.getAccent().withAlpha(0.5f);
    protected AutreRenderer2.Color selectedColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color disabledColor = AutreRenderer2.Color.SECONDARY;
    
    protected Runnable onSelectionChange;
    
    public AutreRadioBox(float x, float y, String label, Object value) {
        super(x, y, 200f, 20f);
        this.label = label;
        this.value = value;
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreRadioBox setLabel(String label) {
        this.label = label;
        return this;
    }
    
    public AutreRadioBox setValue(Object value) {
        this.value = value;
        return this;
    }
    
    public Object getValue() {
        return value;
    }
    
    public AutreRadioBox setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            if (group != null) {
                group.handleSelectionChange(this);
            }
            if (onSelectionChange != null) {
                onSelectionChange.run();
            }
        }
        return this;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public AutreRadioBox setGroup(AutreRadioGroup group) {
        this.group = group;
        return this;
    }
    
    public AutreRadioBox setOnSelectionChange(Runnable callback) {
        this.onSelectionChange = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float radioX = getAbsoluteX();
        float radioY = getAbsoluteY() + (height - radioSize) / 2f;
        
        if (event.x >= radioX && event.x <= radioX + radioSize &&
            event.y >= radioY && event.y <= radioY + radioSize) {
            setSelected(true);
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float radioX = getAbsoluteX();
        float radioY = getAbsoluteY() + (height - radioSize) / 2f;
        
        // Radio button background - flat square (modern flat design)
        AutreRenderer2.Color currentBgColor = enabled ? backgroundColor : disabledColor.darker(0.2f);
        AutreRenderer2.fillRect(context.getMatrices(),
            radioX, radioY, radioSize, radioSize, currentBgColor);
        
        // Border
        AutreRenderer2.Color currentBorderColor = enabled ? borderColor : disabledColor;
        AutreRenderer2.fillRect(context.getMatrices(),
            radioX, radioY, radioSize, 1f, currentBorderColor);
        AutreRenderer2.fillRect(context.getMatrices(),
            radioX, radioY + radioSize - 1f, radioSize, 1f, currentBorderColor);
        AutreRenderer2.fillRect(context.getMatrices(),
            radioX, radioY, 1f, radioSize, currentBorderColor);
        AutreRenderer2.fillRect(context.getMatrices(),
            radioX + radioSize - 1f, radioY, 1f, radioSize, currentBorderColor);
        
        // Selection indicator - flat square fill
        if (selected) {
            float innerPadding = 3f;
            AutreRenderer2.fillRect(context.getMatrices(),
                radioX + innerPadding, radioY + innerPadding,
                radioSize - innerPadding * 2, radioSize - innerPadding * 2, selectedColor);
        }
        
        // Label text
        if (label != null && !label.isEmpty()) {
            float textX = radioX + radioSize + labelSpacing;
            float textY = getAbsoluteY() + (height - mc.textRenderer.fontHeight) / 2f;
            
            AutreRenderer2.Color currentTextColor = enabled ? textColor : disabledColor;
            AutreRenderer2.drawText(context, label, textX, textY, currentTextColor, false);
        }
    }
}