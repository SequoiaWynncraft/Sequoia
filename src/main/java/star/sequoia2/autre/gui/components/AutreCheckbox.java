package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Simple checkbox component with square filled/unfilled appearance
 */
public class AutreCheckbox extends AutreComponent {
    protected boolean checked = false;
    protected String label = "";
    protected boolean showLabel = true;
    
    protected float checkboxSize = 16f;
    protected float labelSpacing = 8f;
    
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color borderColor;
    protected AutreRenderer2.Color fillColor;
    protected AutreRenderer2.Color labelColor;
    protected AutreRenderer2.Color disabledColor;
    
    protected EventHandler<AutreCheckboxChangeEvent> onChangeHandler;
    
    public AutreCheckbox(float x, float y, String label) {
        this(x, y, 200f, 20f, label);
    }
    
    public AutreCheckbox(float x, float y, float width, float height, String label) {
        super(x, y, width, height);
        this.label = label;
        
        // Colors matching the mockups - simple and flat
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND;
        this.borderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.3f);
        this.fillColor = AutreRenderer2.Color.getAccent();
        this.labelColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.disabledColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
        
        // Add event handler
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                onMouseClick(event);
            }
        });
    }
    
    public AutreCheckbox setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (onChangeHandler != null) {
                onChangeHandler.handle(new AutreCheckboxChangeEvent(this, checked));
            }
        }
        return this;
    }
    
    public boolean isChecked() {
        return checked;
    }
    
    public AutreCheckbox setLabel(String label) {
        this.label = label;
        return this;
    }
    
    public String getLabel() {
        return label;
    }
    
    public AutreCheckbox setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }
    
    public AutreCheckbox setCheckboxSize(float size) {
        this.checkboxSize = size;
        return this;
    }
    
    public AutreCheckbox setLabelSpacing(float spacing) {
        this.labelSpacing = spacing;
        return this;
    }
    
    public AutreCheckbox setOnChange(EventHandler<AutreCheckboxChangeEvent> handler) {
        this.onChangeHandler = handler;
        return this;
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled || !event.isPressed()) return;
        
        float checkboxX = getAbsoluteX() + 4f;
        float checkboxY = getAbsoluteY() + (height - checkboxSize) / 2f;
        
        // Check if click is within checkbox area
        if (event.x >= checkboxX && event.x <= checkboxX + checkboxSize &&
            event.y >= checkboxY && event.y <= checkboxY + checkboxSize) {
            setChecked(!checked);
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        float checkboxX = getAbsoluteX() + 4f;
        float checkboxY = getAbsoluteY() + (height - checkboxSize) / 2f;
        
        // Check if mouse is hovering over checkbox
        boolean isHovered = enabled && mouseX >= checkboxX && mouseX <= checkboxX + checkboxSize &&
                           mouseY >= checkboxY && mouseY <= checkboxY + checkboxSize;
        
        // Determine colors based on enabled and hover state
        AutreRenderer2.Color currentBorderColor;
        AutreRenderer2.Color currentFillColor;
        AutreRenderer2.Color currentLabelColor;
        
        if (!enabled) {
            currentBorderColor = disabledColor;
            currentFillColor = disabledColor;
            currentLabelColor = disabledColor;
        } else if (isHovered) {
            currentBorderColor = borderColor.lighter(0.2f);
            currentFillColor = fillColor.lighter(0.1f);
            currentLabelColor = labelColor.lighter(0.1f);
        } else {
            currentBorderColor = borderColor;
            currentFillColor = fillColor;
            currentLabelColor = labelColor;
        }
        
        // Render checkbox background (empty square)
        AutreRenderer2.Color bgColor = isHovered ? backgroundColor.lighter(0.1f) : backgroundColor;
        AutreRenderer2.fillRect(context.getMatrices(),
            checkboxX, checkboxY, checkboxSize, checkboxSize, bgColor);
        
        // Render checkbox border
        // Top border
        AutreRenderer2.fillRect(context.getMatrices(),
            checkboxX, checkboxY, checkboxSize, 1f, currentBorderColor);
        // Bottom border
        AutreRenderer2.fillRect(context.getMatrices(),
            checkboxX, checkboxY + checkboxSize - 1f, checkboxSize, 1f, currentBorderColor);
        // Left border
        AutreRenderer2.fillRect(context.getMatrices(),
            checkboxX, checkboxY, 1f, checkboxSize, currentBorderColor);
        // Right border
        AutreRenderer2.fillRect(context.getMatrices(),
            checkboxX + checkboxSize - 1f, checkboxY, 1f, checkboxSize, currentBorderColor);
        
        // Render checkbox fill if checked (solid square inside)
        if (checked) {
            AutreRenderer2.fillRect(context.getMatrices(),
                checkboxX + 3f, checkboxY + 3f, checkboxSize - 6f, checkboxSize - 6f, currentFillColor);
        }
        
        // Render label if enabled
        if (showLabel && label != null && !label.isEmpty()) {
            float labelX = checkboxX + checkboxSize + labelSpacing;
            float labelY = getAbsoluteY() + (height - mc.textRenderer.fontHeight) / 2f;
            
            AutreRenderer2.drawText(context, label,
                (int) labelX, (int) labelY,
                currentLabelColor, false);
        }
    }
    

}