package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * Slider component for numerical value selection
 */
public class AutreSlider extends AutreComponent {
    protected float minValue;
    protected float maxValue;
    protected float currentValue;
    protected boolean isDragging = false;
    protected float sliderWidth = 4f;
    protected float knobWidth = 8f;
    
    // Hover state tracking
    protected boolean isHovered = false;
    protected float targetHoverAlpha = 0.0f;
    protected float currentHoverAlpha = 0.0f;
    
    // Colors
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color hoverColor;
    protected AutreRenderer2.Color trackColor;
    protected AutreRenderer2.Color knobColor;
    protected AutreRenderer2.Color fillColor;
    
    public AutreSlider(float x, float y, float width, float height, 
                      float minValue, float maxValue, float initialValue) {
        super(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = Math.max(minValue, Math.min(maxValue, initialValue));
        
        // Default colors
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND;
        this.trackColor = AutreRenderer2.Color.SURFACE;
        this.fillColor = AutreRenderer2.Color.getAccent();
        this.knobColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.hoverColor = AutreRenderer2.Color.getAccent().lighter(0.2f);
        
        // Add event handlers
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                onMouseClick(event);
            }
        });
        addEventListener(MouseHoverEvent.class, new EventHandler<MouseHoverEvent>() {
            @Override
            public void handle(MouseHoverEvent event) {
                onMouseHover(event);
            }
        });
    }
    
    public AutreSlider setValue(float value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
        return this;
    }
    
    public AutreSlider setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        this.currentValue = Math.max(min, Math.min(max, currentValue));
        return this;
    }
    
    public AutreSlider setTrackColor(AutreRenderer2.Color color) {
        this.trackColor = color;
        return this;
    }
    
    public AutreSlider setKnobColor(AutreRenderer2.Color color) {
        this.knobColor = color;
        return this;
    }
    
    public AutreSlider setKnobWidth(float width) {
        this.knobWidth = width;
        return this;
    }
    
    public AutreSlider setFillColor(AutreRenderer2.Color color) {
        this.fillColor = color;
        return this;
    }
    
    public float getValue() {
        return currentValue;
    }
    
    public float getMinValue() {
        return minValue;
    }
    
    public float getMaxValue() {
        return maxValue;
    }
    
    protected float getProgress() {
        return (currentValue - minValue) / (maxValue - minValue);
    }
    
    protected float getKnobX() {
        return getAbsoluteX() + (width - sliderWidth) * getProgress();
    }
    
    protected void updateValueFromMouse(float mouseX) {
        float relativeX = mouseX - getAbsoluteX();
        float progress = Math.max(0, Math.min(1, relativeX / width));
        currentValue = minValue + progress * (maxValue - minValue);
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled) return;
        
        if (event.pressed) {
            // Calculate knob position to check if clicking on knob
            float fillWidth = (width - knobWidth) * ((currentValue - minValue) / (maxValue - minValue));
            float knobX = getAbsoluteX() + fillWidth;
            
            // Check if clicking on knob
            if (event.x >= knobX && event.x <= knobX + knobWidth &&
                event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height) {
                isDragging = true;
                return;
            }
            
            // Click on track - set value based on position
            // The render method uses: knobX = getAbsoluteX() + fillWidth
            // Where: fillWidth = (width - knobWidth) * ((currentValue - minValue) / (maxValue - minValue))
            // So we need to solve for currentValue when knobX = event.x
            float relativeX = event.x - getAbsoluteX();
            float percentage = Math.max(0, Math.min(1, relativeX / (width - knobWidth)));
            setValue(minValue + percentage * (maxValue - minValue));
        } else {
            isDragging = false;
        }
        
        // Handle dragging
        if (isDragging && event.x >= getAbsoluteX() && event.x <= getAbsoluteX() + width) {
            float relativeX = event.x - getAbsoluteX();
            float percentage = Math.max(0, Math.min(1, relativeX / (width - knobWidth)));
            setValue(minValue + percentage * (maxValue - minValue));
        }
    }
    
    private void onMouseHover(MouseHoverEvent event) {
        if (!enabled) return;
        
        float fillWidth = (width - knobWidth) * ((currentValue - minValue) / (maxValue - minValue));
        float knobX = getAbsoluteX() + fillWidth;
        
        // Check if hovering over knob
        boolean wasHovered = isHovered;
        isHovered = event.x >= knobX && event.x <= knobX + knobWidth &&
                   event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height;
        
        if (isHovered != wasHovered) {
            targetHoverAlpha = isHovered ? 1.0f : 0.0f;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Calculate knob position and size for hover detection (square knob like range slider)
        float fillWidth = (width - knobWidth) * ((currentValue - minValue) / (maxValue - minValue));
        float knobX = getAbsoluteX() + fillWidth;
        float knobY = getAbsoluteY() + (height - knobWidth) / 2;
        
        // Check if hovering over knob or track
        boolean isKnobHovered = enabled && mouseX >= knobX && mouseX <= knobX + knobWidth &&
                               mouseY >= knobY && mouseY <= knobY + knobWidth;
        boolean isTrackHovered = enabled && mouseX >= getAbsoluteX() && mouseX <= getAbsoluteX() + width &&
                                mouseY >= getAbsoluteY() && mouseY <= getAbsoluteY() + height;
        
        // Colors based on enabled and hover state
        AutreRenderer2.Color currentTrackColor;
        AutreRenderer2.Color currentFillColor;
        AutreRenderer2.Color currentKnobColor;
        
        if (!enabled) {
            currentTrackColor = trackColor.darker(0.3f);
            currentFillColor = fillColor.darker(0.3f);
            currentKnobColor = knobColor.darker(0.3f);
        } else if (isKnobHovered || isDragging) {
            currentTrackColor = trackColor.lighter(0.1f);
            currentFillColor = fillColor.lighter(0.1f);
            currentKnobColor = knobColor.lighter(0.2f);
        } else if (isTrackHovered) {
            currentTrackColor = trackColor.lighter(0.05f);
            currentFillColor = fillColor.lighter(0.05f);
            currentKnobColor = knobColor.lighter(0.1f);
        } else {
            currentTrackColor = trackColor;
            currentFillColor = fillColor;
            currentKnobColor = knobColor;
        }
        
        // Render track
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY() + height / 2 - 2f, width, 4f, currentTrackColor);
        
        // Render fill
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY() + height / 2 - 2f, fillWidth, 4f, currentFillColor);
        
        // Render knob (square like range slider)
        AutreRenderer2.fillRect(context.getMatrices(),
            knobX, knobY, knobWidth, knobWidth, currentKnobColor);
    }
}