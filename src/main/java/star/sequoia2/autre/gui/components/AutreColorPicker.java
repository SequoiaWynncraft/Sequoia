package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import static star.sequoia2.client.SeqClient.mc;

import java.util.function.Consumer;

/**
 * Color picker with HSV color wheel and flat design
 */
public class AutreColorPicker extends AutreComponent {
    protected AutreRenderer2.Color selectedColor = AutreRenderer2.Color.WHITE;
    protected boolean isOpen = false;
    
    // HSV values (0-1 range)
    protected float hue = 0f;        // 0-1 (0-360°)
    protected float saturation = 1f; // 0-1
    protected float value = 1f;      // 0-1
    protected float alpha = 1f;      // 0-1
    
    // Picker dimensions
    protected float pickerWidth = 200f;
    protected float pickerHeight = 160f;
    protected float hueBarHeight = 16f;
    protected float alphaBarHeight = 16f;
    protected float previewHeight = 20f;
    protected float spacing = 4f;
    
    // Interaction state
    protected boolean isDraggingSV = false;
    protected boolean isDraggingHue = false;
    protected boolean isDraggingAlpha = false;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color pickerBg = AutreRenderer2.Color.SURFACE;
    
    protected Consumer<AutreRenderer2.Color> onColorChange;
    
    public AutreColorPicker(float x, float y, float width, float height) {
        super(x, y, width, height);
        updateSelectedColor();
        
        addEventListener(MouseClickEvent.class, this::handleClick);
        addEventListener(MouseHoverEvent.class, this::handleHover);
    }
    
    public AutreColorPicker setColor(AutreRenderer2.Color color) {
        this.selectedColor = color;
        // Convert RGB to HSV
        float[] hsv = rgbToHsv(color.r, color.g, color.b);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];
        this.alpha = color.a;
        return this;
    }
    
    public AutreRenderer2.Color getColor() {
        return selectedColor;
    }
    
    public AutreColorPicker setOnColorChange(Consumer<AutreRenderer2.Color> callback) {
        this.onColorChange = callback;
        return this;
    }
    
    private void updateSelectedColor() {
        float[] rgb = hsvToRgb(hue, saturation, value);
        selectedColor = new AutreRenderer2.Color(rgb[0], rgb[1], rgb[2], alpha);
        
        if (onColorChange != null) {
            onColorChange.accept(selectedColor);
        }
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Check click on main color preview
        if (event.x >= absX && event.x <= absX + width &&
            event.y >= absY && event.y <= absY + height) {
            isOpen = !isOpen;
            return;
        }
        
        // Handle picker interactions when open
        if (isOpen) {
            float pickerX = absX;
            float pickerY = absY + height + 2f;
            
            if (event.x >= pickerX && event.x <= pickerX + pickerWidth &&
                event.y >= pickerY && event.y <= pickerY + pickerHeight) {
                
                handlePickerClick(event.x - pickerX, event.y - pickerY, event.pressed);
                return;
            }
        }
        
        // Click outside - close picker
        if (isOpen && event.pressed) {
            isOpen = false;
            isDraggingSV = false;
            isDraggingHue = false;
            isDraggingAlpha = false;
        }
    }
    
    private void handleHover(MouseHoverEvent event) {
        if (!isOpen || !enabled || !visible) return;
        
        float pickerX = getAbsoluteX();
        float pickerY = getAbsoluteY() + height + 2f;
        
        if (event.x >= pickerX && event.x <= pickerX + pickerWidth &&
            event.y >= pickerY && event.y <= pickerY + pickerHeight) {
            
            handlePickerClick(event.x - pickerX, event.y - pickerY, true);
        }
    }
    
    private void handlePickerClick(float relativeX, float relativeY, boolean pressed) {
        float svAreaHeight = pickerHeight - hueBarHeight - alphaBarHeight - previewHeight - spacing * 3;
        
        // Saturation/Value area
        if (relativeY <= svAreaHeight) {
            if (pressed) isDraggingSV = true;
            if (isDraggingSV) {
                saturation = Math.max(0f, Math.min(1f, relativeX / pickerWidth));
                value = Math.max(0f, Math.min(1f, 1f - (relativeY / svAreaHeight)));
                updateSelectedColor();
            }
            return;
        }
        
        float hueBarY = svAreaHeight + spacing;
        
        // Hue bar
        if (relativeY >= hueBarY && relativeY <= hueBarY + hueBarHeight) {
            if (pressed) isDraggingHue = true;
            if (isDraggingHue) {
                hue = Math.max(0f, Math.min(1f, relativeX / pickerWidth));
                updateSelectedColor();
            }
            return;
        }
        
        float alphaBarY = hueBarY + hueBarHeight + spacing;
        
        // Alpha bar
        if (relativeY >= alphaBarY && relativeY <= alphaBarY + alphaBarHeight) {
            if (pressed) isDraggingAlpha = true;
            if (isDraggingAlpha) {
                alpha = Math.max(0f, Math.min(1f, relativeX / pickerWidth));
                updateSelectedColor();
            }
            return;
        }
        
        // Stop dragging if clicked elsewhere
        if (pressed) {
            isDraggingSV = false;
            isDraggingHue = false;
            isDraggingAlpha = false;
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Main color preview button
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, selectedColor);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX, absY, width, height, 1f, borderColor);
        
        // Color picker popup
        if (isOpen) {
            renderColorPicker(context, absX, absY + height + 2f);
        }
    }
    
    private void renderColorPicker(DrawContext context, float pickerX, float pickerY) {
        // Picker background
        AutreRenderer2.fillRect(context.getMatrices(),
            pickerX, pickerY, pickerWidth, pickerHeight, pickerBg);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            pickerX, pickerY, pickerWidth, pickerHeight, 1f, borderColor);
        
        float svAreaHeight = pickerHeight - hueBarHeight - alphaBarHeight - previewHeight - spacing * 3;
        
        // Saturation/Value area (simplified grid representation)
        renderSVArea(context, pickerX, pickerY, pickerWidth, svAreaHeight);
        
        float hueBarY = pickerY + svAreaHeight + spacing;
        
        // Hue bar
        renderHueBar(context, pickerX, hueBarY, pickerWidth, hueBarHeight);
        
        float alphaBarY = hueBarY + hueBarHeight + spacing;
        
        // Alpha bar
        renderAlphaBar(context, pickerX, alphaBarY, pickerWidth, alphaBarHeight);
        
        float previewY = alphaBarY + alphaBarHeight + spacing;
        
        // Color preview
        AutreRenderer2.fillRect(context.getMatrices(),
            pickerX, previewY, pickerWidth, previewHeight, selectedColor);
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            pickerX, previewY, pickerWidth, previewHeight, 1f, borderColor);
        
        // Hex value text
        String hexValue = String.format("#%02X%02X%02X", 
            (int)(selectedColor.r * 255), 
            (int)(selectedColor.g * 255), 
            (int)(selectedColor.b * 255));
        
        float hexX = pickerX + (pickerWidth - mc.textRenderer.getWidth(hexValue)) / 2f;
        float hexY = previewY + (previewHeight - mc.textRenderer.fontHeight) / 2f;
        
        AutreRenderer2.Color textColor = selectedColor.r + selectedColor.g + selectedColor.b > 1.5f ? 
            AutreRenderer2.Color.BLACK : AutreRenderer2.Color.WHITE;
        
        AutreRenderer2.drawText(context, hexValue, hexX, hexY, textColor, false);
    }
    
    private void renderSVArea(DrawContext context, float x, float y, float w, float h) {
        // Simplified representation - draw base hue color with overlays
        float[] baseRgb = hsvToRgb(hue, 1f, 1f);
        AutreRenderer2.Color baseColor = new AutreRenderer2.Color(baseRgb[0], baseRgb[1], baseRgb[2], 1f);
        
        // Base hue color
        AutreRenderer2.fillRect(context.getMatrices(), x, y, w, h, baseColor);
        
        // White to transparent gradient (saturation)
        for (int i = 0; i < 10; i++) {
            float alpha = 1f - (i / 9f);
            AutreRenderer2.Color whiteOverlay = AutreRenderer2.Color.WHITE.withAlpha(alpha * 0.8f);
            float stripWidth = w / 10f;
            AutreRenderer2.fillRect(context.getMatrices(), x + i * stripWidth, y, stripWidth, h, whiteOverlay);
        }
        
        // Black overlay for value
        for (int i = 0; i < 10; i++) {
            float alpha = i / 9f;
            AutreRenderer2.Color blackOverlay = AutreRenderer2.Color.BLACK.withAlpha(alpha * 0.8f);
            float stripHeight = h / 10f;
            AutreRenderer2.fillRect(context.getMatrices(), x, y + i * stripHeight, w, stripHeight, blackOverlay);
        }
        
        // Current selection indicator
        float indicatorX = x + saturation * w - 2f;
        float indicatorY = y + (1f - value) * h - 2f;
        
        AutreRenderer2.strokeRect(context.getMatrices(),
            indicatorX, indicatorY, 4f, 4f, 1f, AutreRenderer2.Color.WHITE);
    }
    
    private void renderHueBar(DrawContext context, float x, float y, float w, float h) {
        // Rainbow hue bar
        int segments = 20;
        float segmentWidth = w / segments;
        
        for (int i = 0; i < segments; i++) {
            float hueValue = (float) i / segments;
            float[] rgb = hsvToRgb(hueValue, 1f, 1f);
            AutreRenderer2.Color hueColor = new AutreRenderer2.Color(rgb[0], rgb[1], rgb[2], 1f);
            
            AutreRenderer2.fillRect(context.getMatrices(),
                x + i * segmentWidth, y, segmentWidth, h, hueColor);
        }
        
        // Current hue indicator
        float indicatorX = x + hue * w - 1f;
        AutreRenderer2.fillRect(context.getMatrices(),
            indicatorX, y, 2f, h, AutreRenderer2.Color.WHITE);
    }
    
    private void renderAlphaBar(DrawContext context, float x, float y, float w, float h) {
        // Checkered background for transparency
        float checkSize = 4f;
        for (float checkX = x; checkX < x + w; checkX += checkSize) {
            for (float checkY = y; checkY < y + h; checkY += checkSize) {
                boolean isWhite = (((int)(checkX / checkSize) + (int)(checkY / checkSize)) % 2) == 0;
                AutreRenderer2.Color checkColor = isWhite ? AutreRenderer2.Color.WHITE : AutreRenderer2.Color.SECONDARY;
                
                float actualWidth = Math.min(checkSize, x + w - checkX);
                float actualHeight = Math.min(checkSize, y + h - checkY);
                
                AutreRenderer2.fillRect(context.getMatrices(),
                    checkX, checkY, actualWidth, actualHeight, checkColor);
            }
        }
        
        // Alpha gradient
        int segments = 20;
        float segmentWidth = w / segments;
        
        for (int i = 0; i < segments; i++) {
            float alphaValue = (float) i / segments;
            AutreRenderer2.Color alphaColor = new AutreRenderer2.Color(
                selectedColor.r, selectedColor.g, selectedColor.b, alphaValue);
            
            AutreRenderer2.fillRect(context.getMatrices(),
                x + i * segmentWidth, y, segmentWidth, h, alphaColor);
        }
        
        // Current alpha indicator
        float indicatorX = x + alpha * w - 1f;
        AutreRenderer2.fillRect(context.getMatrices(),
            indicatorX, y, 2f, h, AutreRenderer2.Color.BLACK);
    }
    
    // Color conversion utilities
    private float[] hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1f - Math.abs(((h * 6f) % 2f) - 1f));
        float m = v - c;
        
        float r, g, b;
        int hSector = (int)(h * 6f);
        
        switch (hSector) {
            case 0: r = c; g = x; b = 0f; break;
            case 1: r = x; g = c; b = 0f; break;
            case 2: r = 0f; g = c; b = x; break;
            case 3: r = 0f; g = x; b = c; break;
            case 4: r = x; g = 0f; b = c; break;
            case 5: r = c; g = 0f; b = x; break;
            default: r = c; g = x; b = 0f; break;
        }
        
        return new float[]{r + m, g + m, b + m};
    }
    
    private float[] rgbToHsv(float r, float g, float b) {
        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float delta = max - min;
        
        float h = 0f;
        if (delta > 0f) {
            if (max == r) {
                h = ((g - b) / delta) % 6f;
            } else if (max == g) {
                h = (b - r) / delta + 2f;
            } else {
                h = (r - g) / delta + 4f;
            }
            h /= 6f;
            if (h < 0f) h += 1f;
        }
        
        float s = max == 0f ? 0f : delta / max;
        float v = max;
        
        return new float[]{h, s, v};
    }
}