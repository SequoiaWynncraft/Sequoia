package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * Stepped slider that snaps to discrete values
 */
public class AutreSteppedSlider extends AutreComponent {
    protected float minValue;
    protected float maxValue;
    protected float stepSize;
    protected float currentValue;
    protected boolean isDragging = false;
    
    // Visual properties
    protected float sliderHeight = 4f;
    protected float knobSize = 8f;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.BACKGROUND;
    protected AutreRenderer2.Color trackColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color fillColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color knobColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color stepColor = AutreRenderer2.Color.getAccent().withAlpha(0.5f);
    
    protected Runnable onValueChange;
    
    public AutreSteppedSlider(float x, float y, float width, float height, 
                             float minValue, float maxValue, float stepSize, float initialValue) {
        super(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.currentValue = snapToStep(Math.max(minValue, Math.min(maxValue, initialValue)));
        
        addEventListener(MouseClickEvent.class, this::handleClick);
        addEventListener(MouseHoverEvent.class, this::handleHover);
    }
    
    public AutreSteppedSlider setRange(float min, float max, float step) {
        this.minValue = min;
        this.maxValue = max;
        this.stepSize = step;
        this.currentValue = snapToStep(Math.max(min, Math.min(max, currentValue)));
        return this;
    }
    
    public AutreSteppedSlider setValue(float value) {
        float newValue = snapToStep(Math.max(minValue, Math.min(maxValue, value)));
        if (newValue != currentValue) {
            this.currentValue = newValue;
            if (onValueChange != null) {
                onValueChange.run();
            }
        }
        return this;
    }
    
    public float getValue() {
        return currentValue;
    }
    
    public AutreSteppedSlider setOnValueChange(Runnable callback) {
        this.onValueChange = callback;
        return this;
    }
    
    private float snapToStep(float value) {
        float steps = Math.round((value - minValue) / stepSize);
        return minValue + steps * stepSize;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible) return;
        
        if (event.pressed) {
            isDragging = true;
            updateValueFromMouse(event.x);
        } else {
            isDragging = false;
        }
    }
    
    private void handleHover(MouseHoverEvent event) {
        if (!enabled || !visible || !isDragging) return;
        
        updateValueFromMouse(event.x);
    }
    
    private void updateValueFromMouse(float mouseX) {
        float relativeX = mouseX - getAbsoluteX();
        float effectiveWidth = width - knobSize;
        float percentage = Math.max(0f, Math.min(1f, relativeX / effectiveWidth));
        float newValue = minValue + percentage * (maxValue - minValue);
        setValue(newValue);
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float trackY = getAbsoluteY() + (height - sliderHeight) / 2f;
        
        // Background track
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), trackY, width, sliderHeight, trackColor);
        
        // Step indicators (grooves)
        int totalSteps = (int) ((maxValue - minValue) / stepSize);
        if (totalSteps > 1) {
            float stepWidth = (width - knobSize) / (totalSteps);
            
            for (int i = 0; i <= totalSteps; i++) {
                float stepX = getAbsoluteX() + i * stepWidth;
                
                // Step groove indicator - small vertical line
                AutreRenderer2.fillRect(context.getMatrices(),
                    stepX, trackY - 2f, 1f, sliderHeight + 4f, stepColor);
            }
        }
        
        // Fill (from start to current position)
        float progress = (currentValue - minValue) / (maxValue - minValue);
        float fillWidth = progress * (width - knobSize);
        
        if (fillWidth > 0) {
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), trackY, fillWidth, sliderHeight, fillColor);
        }
        
        // Knob (flat square)
        float knobX = getAbsoluteX() + fillWidth;
        float knobY = getAbsoluteY() + (height - knobSize) / 2f;
        
        // Knob background
        AutreRenderer2.Color currentKnobColor = knobColor;
        if (isDragging) {
            currentKnobColor = knobColor.lighter(0.2f);
        } else if (enabled && mouseX >= knobX && mouseX <= knobX + knobSize &&
                  mouseY >= knobY && mouseY <= knobY + knobSize) {
            currentKnobColor = knobColor.lighter(0.1f);
        }
        
        AutreRenderer2.fillRect(context.getMatrices(),
            knobX, knobY, knobSize, knobSize, currentKnobColor);
    }
}