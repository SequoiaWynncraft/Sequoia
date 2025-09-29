package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * Circular knob component for value selection
 */
public class AutreKnob extends AutreComponent {
    protected float minValue = 0f;
    protected float maxValue = 100f;
    protected float currentValue = 50f;
    
    protected boolean isDragging = false;
    protected float startAngle = -135f; // Start at bottom-left
    protected float endAngle = 135f;   // End at bottom-right
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color trackColor = AutreRenderer2.Color.BACKGROUND_SECONDARY;
    protected AutreRenderer2.Color fillColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color knobColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color centerColor = AutreRenderer2.Color.BACKGROUND;
    
    protected float trackWidth = 6f;
    protected float knobRadius;
    
    protected Runnable onValueChange;
    
    public AutreKnob(float x, float y, float size) {
        super(x, y, size, size);
        this.knobRadius = size / 2f - trackWidth;
        
        addEventListener(MouseClickEvent.class, this::handleClick);
        addEventListener(MouseHoverEvent.class, this::handleHover);
    }
    
    public AutreKnob setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        this.currentValue = Math.max(min, Math.min(max, currentValue));
        return this;
    }
    
    public AutreKnob setValue(float value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
        if (onValueChange != null) {
            onValueChange.run();
        }
        return this;
    }
    
    public float getValue() {
        return currentValue;
    }
    
    public AutreKnob setOnValueChange(Runnable callback) {
        this.onValueChange = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible) return;
        
        float centerX = getAbsoluteX() + width / 2f;
        float centerY = getAbsoluteY() + height / 2f;
        
        if (event.pressed) {
            float distance = (float) Math.sqrt(Math.pow(event.x - centerX, 2) + Math.pow(event.y - centerY, 2));
            if (distance <= knobRadius + trackWidth) {
                isDragging = true;
                updateValueFromMouse(event.x, event.y);
            }
        } else {
            isDragging = false;
        }
    }
    
    private void handleHover(MouseHoverEvent event) {
        if (!enabled || !visible || !isDragging) return;
        
        updateValueFromMouse(event.x, event.y);
    }
    
    private void updateValueFromMouse(float mouseX, float mouseY) {
        float centerX = getAbsoluteX() + width / 2f;
        float centerY = getAbsoluteY() + height / 2f;
        
        // Calculate angle from center
        float angle = (float) Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
        
        // Normalize angle to 0-360
        if (angle < 0) angle += 360;
        
        // Convert to our knob range
        float normalizedAngle;
        if (angle >= 315 || angle <= 45) { // Right side
            normalizedAngle = (angle <= 45) ? angle + 360 : angle;
            normalizedAngle = (normalizedAngle - 315) / 90f; // 0-1 range
        } else if (angle >= 45 && angle <= 225) { // Top and left
            normalizedAngle = Math.min(1f, (angle - 45) / 180f);
        } else { // Bottom
            normalizedAngle = Math.max(0f, (315 - angle) / 90f);
        }
        
        normalizedAngle = Math.max(0f, Math.min(1f, normalizedAngle));
        setValue(minValue + normalizedAngle * (maxValue - minValue));
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float centerX = getAbsoluteX() + width / 2f;
        float centerY = getAbsoluteY() + height / 2f;
        float radius = knobRadius;
        
        // Background circle (filled)
        AutreRenderer2.fillCircle(context.getMatrices(), centerX, centerY, radius + trackWidth / 2f, backgroundColor);
        
        // Track circle (stroke outline)
        AutreRenderer2.strokeCircle(context.getMatrices(), centerX, centerY, radius, trackWidth, trackColor);
        
        // Progress arc
        float progress = (currentValue - minValue) / (maxValue - minValue);
        if (progress > 0) {
            float startAngleRad = (float) Math.toRadians(startAngle);
            float progressAngleRad = (float) Math.toRadians(startAngle + progress * (endAngle - startAngle));
            
            // Draw progress arc as stroke
            AutreRenderer2.strokeArc(context.getMatrices(), centerX, centerY, radius, 
                                   startAngleRad, progressAngleRad, trackWidth, fillColor);
        }
        
        // Center knob circle
        AutreRenderer2.fillCircle(context.getMatrices(), centerX, centerY, radius * 0.4f, centerColor);
        
        // Knob indicator dot
        float knobAngle = startAngle + progress * (endAngle - startAngle);
        float knobIndicatorRadius = radius * 0.75f;
        float knobX = centerX + (float) (Math.cos(Math.toRadians(knobAngle)) * knobIndicatorRadius);
        float knobY = centerY + (float) (Math.sin(Math.toRadians(knobAngle)) * knobIndicatorRadius);
        
        // Indicator dot
        AutreRenderer2.fillCircle(context.getMatrices(), knobX, knobY, 3f, knobColor);
        
        // Optional: Add a subtle highlight on hover/drag
        if (isDragging || (mouseX >= getAbsoluteX() && mouseX <= getAbsoluteX() + width &&
                          mouseY >= getAbsoluteY() && mouseY <= getAbsoluteY() + height)) {
            AutreRenderer2.strokeCircle(context.getMatrices(), centerX, centerY, radius * 0.4f, 2f, 
                                      knobColor.withAlpha(0.3f));
        }
    }
}