package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Text input field component
 */
public class AutreTextInput extends AutreComponent {
    protected String text = "";
    protected String placeholder = "";
    protected int cursorPosition = 0;
    protected boolean isPasswordField = false;
    protected int maxLength = 256;
    
    protected AutreRenderer2.Color backgroundColor;
    protected AutreRenderer2.Color borderColor;
    protected AutreRenderer2.Color focusedBorderColor;
    protected AutreRenderer2.Color textColor;
    protected AutreRenderer2.Color placeholderColor;
    protected AutreRenderer2.Color cursorColor;
    
    protected float padding = 8f;
    protected long lastCursorBlink = 0;
    protected boolean showCursor = true;
    
    public AutreTextInput(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        // Default colors
        this.backgroundColor = AutreRenderer2.Color.SURFACE;
        this.borderColor = AutreRenderer2.Color.SURFACE.lighter(0.2f);
        this.focusedBorderColor = AutreRenderer2.Color.getAccent();
        this.textColor = AutreRenderer2.Color.TEXT_PRIMARY;
        this.placeholderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
        this.cursorColor = AutreRenderer2.Color.getAccent();
        
        // Add event handlers
        addEventListener(MouseClickEvent.class, new EventHandler<MouseClickEvent>() {
            @Override
            public void handle(MouseClickEvent event) {
                if (contains(event.x, event.y) && event.isPressed()) {
                    setFocused(true);
                    // Calculate cursor position from mouse click
                    updateCursorFromMouse(event.x);
                }
            }
        });
        
        addEventListener(KeyEvent.class, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (focused && event.isPressed()) {
                    handleKeyInput(event.keyCode, (char) event.keyCode);
                }
            }
        });
    }
    
    public AutreTextInput setText(String text) {
        this.text = text == null ? "" : text;
        this.cursorPosition = Math.min(this.text.length(), cursorPosition);
        return this;
    }
    
    public AutreTextInput setPlaceholder(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        return this;
    }
    
    public AutreTextInput setPasswordField(boolean isPassword) {
        this.isPasswordField = isPassword;
        return this;
    }
    
    public AutreTextInput setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
        return this;
    }
    
    public AutreTextInput setBackgroundColor(AutreRenderer2.Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public AutreTextInput setBorderColor(AutreRenderer2.Color color) {
        this.borderColor = color;
        return this;
    }
    
    public AutreTextInput setFocusedBorderColor(AutreRenderer2.Color color) {
        this.focusedBorderColor = color;
        return this;
    }
    
    public String getText() {
        return text;
    }
    
    private void updateCursorFromMouse(float mouseX) {
        float textX = getAbsoluteX() + padding;
        float relativeX = mouseX - textX;
        
        // Find cursor position based on mouse position
        String displayText = getDisplayText();
        int bestPosition = 0;
        float bestDistance = Math.abs(relativeX);
        
        for (int i = 0; i <= displayText.length(); i++) {
            String substr = displayText.substring(0, i);
            float textWidth = mc.textRenderer.getWidth(substr);
            float distance = Math.abs(relativeX - textWidth);
            
            if (distance < bestDistance) {
                bestDistance = distance;
                bestPosition = i;
            }
        }
        
        cursorPosition = bestPosition;
    }
    
    private void handleKeyInput(int keyCode, char character) {
        switch (keyCode) {
            case 259: // Backspace
                if (cursorPosition > 0 && !text.isEmpty()) {
                    text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                    cursorPosition--;
                }
                break;
            case 261: // Delete
                if (cursorPosition < text.length()) {
                    text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                }
                break;
            case 262: // Right arrow
                cursorPosition = Math.min(text.length(), cursorPosition + 1);
                break;
            case 263: // Left arrow
                cursorPosition = Math.max(0, cursorPosition - 1);
                break;
            case 268: // Home
                cursorPosition = 0;
                break;
            case 269: // End
                cursorPosition = text.length();
                break;
            default:
                // Regular character input
                if (isPrintable(character) && text.length() < maxLength) {
                    text = text.substring(0, cursorPosition) + character + text.substring(cursorPosition);
                    cursorPosition++;
                }
                break;
        }
    }
    
    private boolean isPrintable(char character) {
        return character >= 32 && character <= 126; // Basic ASCII printable range
    }
    
    private String getDisplayText() {
        if (isPasswordField) {
            return "*".repeat(text.length());
        }
        return text;
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        // Update cursor blink
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCursorBlink > 500) {
            showCursor = !showCursor;
            lastCursorBlink = currentTime;
        }
        
        // Render background
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        
        // Only subtle bottom border when focused
        if (focused) {
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), getAbsoluteY() + height - 1, width, 1, focusedBorderColor);
        }
        
        // Render text or placeholder
        float textX = getAbsoluteX() + padding;
        float textY = getAbsoluteY() + (height - mc.textRenderer.fontHeight) / 2;
        
        String displayText = getDisplayText();
        if (displayText.isEmpty() && !placeholder.isEmpty() && !focused) {
            // Show placeholder
            context.drawText(mc.textRenderer, placeholder, (int) textX, (int) textY,
                placeholderColor.toRGB(), false);
        } else {
            // Show text
            context.drawText(mc.textRenderer, displayText, (int) textX, (int) textY,
                textColor.toRGB(), false);
        }
        
        // Render cursor
        if (focused && showCursor && cursorPosition <= displayText.length()) {
            String beforeCursor = displayText.substring(0, cursorPosition);
            float cursorX = textX + mc.textRenderer.getWidth(beforeCursor);
            
            AutreRenderer2.fillRect(context.getMatrices(),
                cursorX, textY, 1, mc.textRenderer.fontHeight, cursorColor);
        }
    }
}