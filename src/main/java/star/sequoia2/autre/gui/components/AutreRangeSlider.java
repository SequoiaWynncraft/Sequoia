package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Range slider component for selecting a range between two values
 */
public class AutreRangeSlider extends AutreComponent {
    protected float minValue;
    protected float maxValue;
    protected float lowerValue;
    protected float upperValue;
    
    protected boolean isDraggingLower = false;
    protected boolean isDraggingUpper = false;
    protected float knobWidth = 8f;
    protected float trackHeight = 4f;
    
    // Hover state tracking
    protected boolean isMinHovered = false;
    protected boolean isMaxHovered = false;
    
    // Colors
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color trackColor;
    protected AutreRenderer2.Color fillColor;
    protected AutreRenderer2.Color knobColor;
    protected AutreRenderer2.Color activeKnobColor;
    
    public AutreRangeSlider(float x, float y, float width, float height, 
                           float minValue, float maxValue, float initialLower, float initialUpper) {
        super(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.lowerValue = Math.max(minValue, Math.min(maxValue, initialLower));
        this.upperValue = Math.max(lowerValue, Math.min(maxValue, initialUpper));
        
        // Default colors
        this.backgroundColor = AutreRenderer2.Color.BACKGROUND;
        this.trackColor = AutreRenderer2.Color.SURFACE;
        this.fillColor = AutreRenderer2.Color.getAccent();
        this.knobColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.activeKnobColor = AutreRenderer2.Color.getAccent();
        
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
    
    public AutreRangeSlider setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        this.lowerValue = Math.max(min, Math.min(max, lowerValue));
        this.upperValue = Math.max(lowerValue, Math.min(max, upperValue));
        return this;
    }
    
    public AutreRangeSlider setValues(float lower, float upper) {
        this.lowerValue = Math.max(minValue, Math.min(maxValue, lower));
        this.upperValue = Math.max(lowerValue, Math.min(maxValue, upper));
        return this;
    }
    
    public AutreRangeSlider setTrackColor(AutreRenderer2.Color color) {
        this.trackColor = color;
        return this;
    }
    
    public AutreRangeSlider setFillColor(AutreRenderer2.Color color) {
        this.fillColor = color;
        return this;
    }
    
    public AutreRangeSlider setKnobColor(AutreRenderer2.Color color) {
        this.knobColor = color;
        return this;
    }
    
    public float getLowerValue() { return lowerValue; }
    public float getUpperValue() { return upperValue; }
    public float getMinValue() { return minValue; }
    public float getMaxValue() { return maxValue; }
    
    protected float getLowerProgress() {
        return (lowerValue - minValue) / (maxValue - minValue);
    }
    
    protected float getUpperProgress() {
        return (upperValue - minValue) / (maxValue - minValue);
    }
    
    protected float getLowerKnobX() {
        return getAbsoluteX() + (width - knobWidth) * getLowerProgress();
    }
    
    protected float getUpperKnobX() {
        return getAbsoluteX() + (width - knobWidth) * getUpperProgress();
    }
    
    protected void updateLowerValueFromMouse(float mouseX) {
        float relativeX = mouseX - getAbsoluteX();
        // Match the knob positioning formula: knobX = getAbsoluteX() + (width - knobWidth) * progress
        float progress = Math.max(0, Math.min(1, relativeX / (width - knobWidth)));
        lowerValue = minValue + progress * (maxValue - minValue);
        
        // Ensure lower doesn't exceed upper
        if (lowerValue > upperValue) {
            lowerValue = upperValue;
        }
    }
    
    protected void updateUpperValueFromMouse(float mouseX) {
        float relativeX = mouseX - getAbsoluteX();
        // Match the knob positioning formula: knobX = getAbsoluteX() + (width - knobWidth) * progress
        float progress = Math.max(0, Math.min(1, relativeX / (width - knobWidth)));
        upperValue = minValue + progress * (maxValue - minValue);
        
        // Ensure upper doesn't go below lower
        if (upperValue < lowerValue) {
            upperValue = lowerValue;
        }
    }
    
    private void onMouseClick(MouseClickEvent event) {
        if (!enabled) return;
        
        float lowerKnobX = getAbsoluteX() + (lowerValue - minValue) / (maxValue - minValue) * (width - knobWidth);
        float upperKnobX = getAbsoluteX() + (upperValue - minValue) / (maxValue - minValue) * (width - knobWidth);
        
        if (event.isPressed()) {
            // Check if click is on lower knob
            if (event.x >= lowerKnobX && event.x <= lowerKnobX + knobWidth &&
                event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height) {
                isDraggingLower = true;
                return;
            }
            
            // Check if click is on upper knob
            if (event.x >= upperKnobX && event.x <= upperKnobX + knobWidth &&
                event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height) {
                isDraggingUpper = true;
                return;
            }
        } else {
            isDraggingLower = false;
            isDraggingUpper = false;
        }
        
        // Handle dragging
        if (event.x >= getAbsoluteX() && event.x <= getAbsoluteX() + width) {
            float relativeX = event.x - getAbsoluteX();
            float percentage = Math.max(0, Math.min(1, relativeX / (width - knobWidth)));
            float newValue = minValue + percentage * (maxValue - minValue);
            
            if (isDraggingLower) {
                lowerValue = Math.min(newValue, upperValue);
            } else if (isDraggingUpper) {
                upperValue = Math.max(newValue, lowerValue);
            }
        }
    }
    
    private void onMouseHover(MouseHoverEvent event) {
        if (!enabled) return;
        
        float minKnobX = getAbsoluteX() + (lowerValue - minValue) / (maxValue - minValue) * (width - knobWidth);
        float maxKnobX = getAbsoluteX() + (upperValue - minValue) / (maxValue - minValue) * (width - knobWidth);
        
        // Check hover states
        boolean wasMinHovered = isMinHovered;
        boolean wasMaxHovered = isMaxHovered;
        
        isMinHovered = event.x >= minKnobX && event.x <= minKnobX + knobWidth &&
                      event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height;
                      
        isMaxHovered = event.x >= maxKnobX && event.x <= maxKnobX + knobWidth &&
                      event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height;
        
        if (isDraggingLower) {
            updateLowerValueFromMouse(event.x);
        } else if (isDraggingUpper) {
            updateUpperValueFromMouse(event.x);
        }
        
        // Stop dragging when mouse is no longer pressed
        if (!event.entered) {
            isDraggingLower = false;
            isDraggingUpper = false;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        float trackY = getAbsoluteY() + (height - trackHeight) / 2;
        
        // Render track background
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), trackY, width, trackHeight, trackColor);
        
        // Render filled range
        float lowerKnobX = getLowerKnobX();
        float upperKnobX = getUpperKnobX();
        float rangeStart = lowerKnobX + knobWidth / 2;
        float rangeEnd = upperKnobX + knobWidth / 2;
        float rangeWidth = rangeEnd - rangeStart;
        
        if (rangeWidth > 0) {
            AutreRenderer2.fillRect(context.getMatrices(),
                rangeStart, trackY, rangeWidth, trackHeight, fillColor);
        }
        
        // Render knobs
        float knobY = getAbsoluteY() + (height - knobWidth) / 2;
        
        // Lower knob
        AutreRenderer2.Color lowerKnobRenderColor = isDraggingLower ? 
            activeKnobColor : knobColor;
        AutreRenderer2.fillRect(context.getMatrices(),
            lowerKnobX, knobY, knobWidth, knobWidth, lowerKnobRenderColor);
        
        // Upper knob
        AutreRenderer2.Color upperKnobRenderColor = isDraggingUpper ? 
            activeKnobColor : knobColor;
        AutreRenderer2.fillRect(context.getMatrices(),
            upperKnobX, knobY, knobWidth, knobWidth, upperKnobRenderColor);
        
        // No borders - clean flat design
    }
}