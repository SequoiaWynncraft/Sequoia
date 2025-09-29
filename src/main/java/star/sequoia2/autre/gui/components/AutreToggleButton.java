package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Toggle button component that can be switched on/off
 */
public class AutreToggleButton extends AutreComponent {
    protected String text;
    protected boolean toggled = false;
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color toggledColor;
    protected AutreRenderer2.Color textColor;
    protected float cornerRadius;
    
    // Event callbacks
    protected EventHandler<MouseClickEvent> onToggle;
    
    public AutreToggleButton(float x, float y, float width, float height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.backgroundColor = AutreRenderer2.Color.SURFACE;
        this.toggledColor = AutreRenderer2.Color.getAccent();
        this.textColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.cornerRadius = 0f; // Flat design - no rounded corners
        
        // Set up event handlers
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                if (enabled && contains(event.x, event.y) && event.isPressed()) {
                    toggle();
                    if (onToggle != null) {
                        onToggle.handle(event);
                    }
                }
            }
        });
    }
    
    public AutreToggleButton setToggled(boolean toggled) {
        this.toggled = toggled;
        return this;
    }
    
    public AutreToggleButton setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public AutreToggleButton setToggledColor(AutreRenderer2.Color color) {
        this.toggledColor = color;
        return this;
    }
    
    public AutreToggleButton setTextColor(AutreRenderer2.Color color) {
        this.textColor = color;
        return this;
    }
    
    public AutreToggleButton setCornerRadius(float radius) {
        this.cornerRadius = radius;
        return this;
    }
    
    public AutreToggleButton onToggle(EventHandler<MouseClickEvent> handler) {
        this.onToggle = handler;
        return this;
    }
    
    public boolean isToggled() {
        return toggled;
    }
    
    public void toggle() {
        this.toggled = !this.toggled;
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Use appropriate background color
        AutreRenderer2.Color bgColor = toggled ? toggledColor : backgroundColor;
        
        // Add hover effect
        if (hovered && enabled) {
            bgColor = bgColor.lighter(0.1f);
        }
        
        // Render background
        if (cornerRadius > 0) {
            AutreRenderer2.fillRoundedRect(context.getMatrices(), 
                getAbsoluteX(), getAbsoluteY(), width, height, cornerRadius, bgColor);
        } else {
            AutreRenderer2.fillRect(context.getMatrices(), 
                getAbsoluteX(), getAbsoluteY(), width, height, bgColor);
        }
        
        // Only subtle bottom border for definition
        if (!toggled) {
            AutreRenderer2.Color borderColor = backgroundColor.lighter(0.1f);
            AutreRenderer2.fillRect(context.getMatrices(), 
                getAbsoluteX(), getAbsoluteY() + height - 1, width, 1, borderColor);
        }
        
        // Render text
        if (text != null && !text.isEmpty()) {
            float textWidth = mc.textRenderer.getWidth(text);
            float textHeight = mc.textRenderer.fontHeight;
            
            float textX = getAbsoluteX() + (width - textWidth) / 2;
            float textY = getAbsoluteY() + (height - textHeight) / 2;
            
            // Use white text on accent color, normal text color otherwise
            AutreRenderer2.Color renderTextColor = toggled ? 
                AutreRenderer2.Color.WHITE : textColor;
            
            context.drawText(mc.textRenderer, text, (int) textX, (int) textY, 
                renderTextColor.toRGB(), false);
        }
    }
}