package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.function.Consumer;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Small/compact text input field with flat design
 */
public class AutreTextInputSmall extends AutreComponent {
    protected String text = "";
    protected String placeholder = "";
    protected boolean isFocused = false;
    protected boolean isPassword = false;
    protected int maxLength = 100;
    protected int cursorPosition = 0;
    protected float cursorBlinkTime = 0f;
    protected boolean showCursor = true;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color focusedBgColor = AutreRenderer2.Color.SURFACE.lighter(0.05f);
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color focusedBorderColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color placeholderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
    protected AutreRenderer2.Color cursorColor = AutreRenderer2.Color.getAccent();
    
    protected Consumer<String> onTextChange;
    protected Runnable onEnterPressed;
    
    public AutreTextInputSmall(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        addEventListener(MouseClickEvent.class, this::handleClick);
        addEventListener(KeyEvent.class, this::handleKeyboard);
    }
    
    public AutreTextInputSmall setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
        if (onTextChange != null) {
            onTextChange.accept(this.text);
        }
        return this;
    }
    
    public String getText() {
        return text;
    }
    
    public AutreTextInputSmall setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        return this;
    }
    
    public AutreTextInputSmall setPassword(boolean isPassword) {
        this.isPassword = isPassword;
        return this;
    }
    
    public AutreTextInputSmall setMaxLength(int maxLength) {
        this.maxLength = Math.max(1, maxLength);
        if (text.length() > this.maxLength) {
            setText(text.substring(0, this.maxLength));
        }
        return this;
    }
    
    public AutreTextInputSmall setOnTextChange(Consumer<String> callback) {
        this.onTextChange = callback;
        return this;
    }
    
    public AutreTextInputSmall setOnEnterPressed(Runnable callback) {
        this.onEnterPressed = callback;
        return this;
    }
    
    public boolean isFocused() {
        return isFocused;
    }
    
    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        boolean wasClicked = event.x >= absX && event.x <= absX + width &&
                            event.y >= absY && event.y <= absY + height;
        
        if (event.pressed && wasClicked) {
            isFocused = true;
            
            // Calculate cursor position from click
            float textStartX = absX + 4f;
            float relativeX = event.x - textStartX;
            
            if (relativeX <= 0) {
                cursorPosition = 0;
            } else {
                String displayText = getDisplayText();
                cursorPosition = findCursorPosition(displayText, relativeX);
                cursorPosition = Math.min(cursorPosition, text.length());
            }
        } else if (event.pressed && !wasClicked) {
            isFocused = false;
        }
    }
    
    private void handleKeyboard(KeyEvent event) {
        if (!isFocused || !enabled || !visible || !event.pressed) return;
        
        if (event.keyCode == 257) { // Enter
            if (onEnterPressed != null) {
                onEnterPressed.run();
            }
        } else if (event.keyCode == 259) { // Backspace
            if (cursorPosition > 0) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
                if (onTextChange != null) {
                    onTextChange.accept(text);
                }
            }
        } else if (event.keyCode == 261) { // Delete
            if (cursorPosition < text.length()) {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                if (onTextChange != null) {
                    onTextChange.accept(text);
                }
            }
        } else if (event.keyCode == 262) { // Right Arrow
            cursorPosition = Math.min(text.length(), cursorPosition + 1);
        } else if (event.keyCode == 263) { // Left Arrow
            cursorPosition = Math.max(0, cursorPosition - 1);
        } else if (event.keyCode == 268) { // Home
            cursorPosition = 0;
        } else if (event.keyCode == 269) { // End
            cursorPosition = text.length();
        }
        // Note: Character input handling would need to be implemented through a separate 
        // character typed event or via direct Minecraft key handling
    }
    
    private String getDisplayText() {
        if (isPassword && !text.isEmpty()) {
            return "•".repeat(text.length());
        }
        return text;
    }
    
    private int findCursorPosition(String displayText, float targetX) {
        if (displayText.isEmpty()) return 0;
        
        for (int i = 0; i <= displayText.length(); i++) {
            String substring = displayText.substring(0, i);
            int width = mc.textRenderer.getWidth(substring);
            if (width >= targetX) {
                return i;
            }
        }
        return displayText.length();
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Update cursor blink
        cursorBlinkTime += deltaTime;
        if (cursorBlinkTime >= 1000f) { // 1 second
            showCursor = !showCursor;
            cursorBlinkTime = 0f;
        }
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Background
        AutreRenderer2.Color currentBgColor = isFocused ? focusedBgColor : backgroundColor;
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, currentBgColor);
        
        // Border
        AutreRenderer2.Color currentBorderColor = isFocused ? focusedBorderColor : borderColor;
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX, absY, width, height, 1f, currentBorderColor);
        
        // Text content
        float textStartX = absX + 4f;
        float textY = absY + (height - mc.textRenderer.fontHeight) / 2f;
        
        if (!text.isEmpty()) {
            String displayText = getDisplayText();
            
            // Clip text if too long
            float availableWidth = width - 8f; // Account for padding
            String clippedText = clipTextToWidth(displayText, availableWidth);
            
            AutreRenderer2.drawText(context, clippedText, textStartX, textY, textColor, false);
        } else if (!placeholder.isEmpty() && !isFocused) {
            // Show placeholder
            float availableWidth = width - 8f;
            String clippedPlaceholder = clipTextToWidth(placeholder, availableWidth);
            AutreRenderer2.drawText(context, clippedPlaceholder, textStartX, textY, placeholderColor, false);
        }
        
        // Cursor (when focused)
        if (isFocused && showCursor && cursorPosition <= text.length()) {
            String beforeCursor = getDisplayText().substring(0, Math.min(cursorPosition, text.length()));
            int cursorX = (int) (textStartX + mc.textRenderer.getWidth(beforeCursor));
            
            AutreRenderer2.fillRect(context.getMatrices(),
                cursorX, textY, 1f, mc.textRenderer.fontHeight, cursorColor);
        }
    }
    
    private String clipTextToWidth(String text, float maxWidth) {
        if (mc.textRenderer.getWidth(text) <= maxWidth) {
            return text;
        }
        
        // Binary search for maximum length that fits
        int left = 0, right = text.length();
        while (left < right) {
            int mid = (left + right + 1) / 2;
            if (mc.textRenderer.getWidth(text.substring(0, mid)) <= maxWidth) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        
        return text.substring(0, left);
    }
}