package star.sequoia2.autre.render.text;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import star.sequoia2.autre.render.AutreRenderer2;
import star.sequoia2.utils.XMLUtils;
import star.sequoia2.utils.text.parser.TeXParser;
import star.sequoia2.client.SeqClient;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * AutreTextRenderer2 - Hybrid text rendering system
 * Can switch between Minecraft's native text renderer and custom text rendering on the fly
 */
public class AutreTextRenderer2 {
    
    // Rendering modes
    public enum RenderMode {
        MINECRAFT,  // Use Minecraft's native text renderer
        CUSTOM,     // Use custom font rendering with AWT
        AUTO        // Automatically choose based on font availability
    }
    
    // Text formats
    public enum TextFormat {
        PLAIN,          // Plain text
        MINECRAFT_JSON, // Minecraft JSON text format
        LATEX,          // LaTeX format using TeXParser
        XFT,           // XMLFormattedText format
        RICH_TEXT      // Simple &-code formatting
    }
    
    // Text style definition
    public static class TextStyle {
        public final String fontFamily;
        public final float size;
        public final boolean bold;
        public final boolean italic;
        public final boolean underline;
        public final boolean strikethrough;
        public final boolean shadow;
        public final boolean outline;
        public final AutreRenderer2.Color color;
        public final AutreRenderer2.Color shadowColor;
        public final AutreRenderer2.Color outlineColor;
        public final float shadowOffsetX;
        public final float shadowOffsetY;
        public final float outlineWidth;
        public final RenderMode renderMode;
        
        private TextStyle(Builder builder) {
            this.fontFamily = builder.fontFamily;
            this.size = builder.size;
            this.bold = builder.bold;
            this.italic = builder.italic;
            this.underline = builder.underline;
            this.strikethrough = builder.strikethrough;
            this.shadow = builder.shadow;
            this.outline = builder.outline;
            this.color = builder.color;
            this.shadowColor = builder.shadowColor;
            this.outlineColor = builder.outlineColor;
            this.shadowOffsetX = builder.shadowOffsetX;
            this.shadowOffsetY = builder.shadowOffsetY;
            this.outlineWidth = builder.outlineWidth;
            this.renderMode = builder.renderMode;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private String fontFamily = "minecraft";
            private float size = 9f;
            private boolean bold = false;
            private boolean italic = false;
            private boolean underline = false;
            private boolean strikethrough = false;
            private boolean shadow = false;
            private boolean outline = false;
            private AutreRenderer2.Color color = AutreRenderer2.Color.TEXT_PRIMARY;
            private AutreRenderer2.Color shadowColor = new AutreRenderer2.Color(0f, 0f, 0f, 0.5f);
            private AutreRenderer2.Color outlineColor = new AutreRenderer2.Color(0f, 0f, 0f, 1f);
            private float shadowOffsetX = 1f;
            private float shadowOffsetY = 1f;
            private float outlineWidth = 1f;
            private RenderMode renderMode = RenderMode.AUTO;
            
            public Builder font(String fontFamily) { this.fontFamily = fontFamily; return this; }
            public Builder size(float size) { this.size = size; return this; }
            public Builder bold(boolean bold) { this.bold = bold; return this; }
            public Builder italic(boolean italic) { this.italic = italic; return this; }
            public Builder underline(boolean underline) { this.underline = underline; return this; }
            public Builder strikethrough(boolean strikethrough) { this.strikethrough = strikethrough; return this; }
            public Builder shadow(boolean shadow) { this.shadow = shadow; return this; }
            public Builder outline(boolean outline) { this.outline = outline; return this; }
            public Builder color(AutreRenderer2.Color color) { this.color = color; return this; }
            public Builder shadowColor(AutreRenderer2.Color shadowColor) { this.shadowColor = shadowColor; return this; }
            public Builder outlineColor(AutreRenderer2.Color outlineColor) { this.outlineColor = outlineColor; return this; }
            public Builder shadowOffset(float x, float y) { this.shadowOffsetX = x; this.shadowOffsetY = y; return this; }
            public Builder outlineWidth(float width) { this.outlineWidth = width; return this; }
            public Builder renderMode(RenderMode mode) { this.renderMode = mode; return this; }
            
            public TextStyle build() {
                return new TextStyle(this);
            }
        }
    }
    
    // Font management
    private static final Map<String, Font> customFonts = new ConcurrentHashMap<>();
    private static final Map<String, Map<Float, Font>> fontCache = new ConcurrentHashMap<>();
    private static final FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
    
    // Minecraft text renderer reference
    private static TextRenderer getMinecraftTextRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }
    
    // Rich text formatting pattern
    private static final Pattern FORMATTING_PATTERN = Pattern.compile("&([0-9a-fklmnor])");
    
    /**
     * Load a custom font from file
     */
    public static boolean loadCustomFont(String fontName, File fontFile) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            customFonts.put(fontName, font);
            fontCache.put(fontName, new ConcurrentHashMap<>());
            System.out.println("Loaded custom font: " + fontName + " from " + fontFile.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to load font " + fontName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load a custom font from resource
     */
    public static boolean loadCustomFont(String fontName, String resourcePath) {
        try {
            InputStream fontStream = AutreTextRenderer2.class.getResourceAsStream(resourcePath);
            if (fontStream == null) {
                throw new Exception("Font resource not found: " + resourcePath);
            }
            
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            customFonts.put(fontName, font);
            fontCache.put(fontName, new ConcurrentHashMap<>());
            fontStream.close();
            System.out.println("Loaded custom font: " + fontName + " from resource " + resourcePath);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to load font " + fontName + " from resource: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get or create a font instance with specific size and style
     */
    private static Font getFont(TextStyle style) {
        if ("minecraft".equals(style.fontFamily)) {
            return null; // Use Minecraft renderer
        }
        
        Font baseFont = customFonts.get(style.fontFamily);
        if (baseFont == null) {
            return null; // Fallback to Minecraft renderer
        }
        
        Map<Float, Font> sizeCache = fontCache.get(style.fontFamily);
        Float cacheKey = style.size + (style.bold ? 1000f : 0f) + (style.italic ? 2000f : 0f);
        
        Font font = sizeCache.get(cacheKey);
        if (font == null) {
            int fontStyle = Font.PLAIN;
            if (style.bold) fontStyle |= Font.BOLD;
            if (style.italic) fontStyle |= Font.ITALIC;
            
            font = baseFont.deriveFont(fontStyle, style.size);
            sizeCache.put(cacheKey, font);
        }
        
        return font;
    }
    
    /**
     * Determine which rendering mode to use
     */
    private static RenderMode determineRenderMode(TextStyle style) {
        if (style.renderMode == RenderMode.MINECRAFT) {
            return RenderMode.MINECRAFT;
        } else if (style.renderMode == RenderMode.CUSTOM) {
            return RenderMode.CUSTOM;
        } else { // AUTO mode
            if ("minecraft".equals(style.fontFamily)) {
                return RenderMode.MINECRAFT;
            } else if (customFonts.containsKey(style.fontFamily)) {
                return RenderMode.CUSTOM;
            } else {
                return RenderMode.MINECRAFT; // Fallback
            }
        }
    }
    
    /**
     * Detect text format from content
     */
    private static TextFormat detectTextFormat(String text) {
        if (text == null || text.isEmpty()) {
            return TextFormat.PLAIN;
        }
        
        // Check for XMLFormattedText
        if (text.trim().startsWith("<XMLFormattedText")) {
            return TextFormat.XFT;
        }
        
        // Check for JSON format (starts with { or [)
        String trimmed = text.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || 
            (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return TextFormat.MINECRAFT_JSON;
        }
        
        // Check for LaTeX format (contains LaTeX commands)
        if (text.contains("\\") && (text.contains("{") || text.contains("}"))) {
            return TextFormat.LATEX;
        }
        
        // Check for rich text format (contains & codes)
        if (text.contains("&") && FORMATTING_PATTERN.matcher(text).find()) {
            return TextFormat.RICH_TEXT;
        }
        
        return TextFormat.PLAIN;
    }
    
    /**
     * Preprocess text based on format
     */
    private static String preprocessText(String text, TextFormat format) {
        switch (format) {
            case XFT:
                return XMLUtils.extractTextFromXml(text);
            case LATEX:
                // Convert LaTeX to plain text using TeXParser
                try {
                    TeXParser parser = SeqClient.getTeXParser();
                    if (parser != null) {
                        // i dont know the implementation of latex and star is slacking off
                        return parser.parseMutableText(text).getString();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse LaTeX: " + e.getMessage());
                }
                return text; // Fallback to original
            case MINECRAFT_JSON:
                // Convert JSON text to plain text
                try {
                    Text minecraftText = Text.Serialization.fromJson(text, MinecraftClient.getInstance().world.getRegistryManager());
                    if (minecraftText != null) {
                        return minecraftText.getString();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse Minecraft JSON: " + e.getMessage());
                }
                return text; // Fallback to original
            case RICH_TEXT:
                // Keep rich text as-is, it will be processed during rendering
                return text;
            case PLAIN:
            default:
                return text;
        }
    }
    
    /**
     * Draw text using the appropriate renderer with format detection
     */
    public static void drawText(DrawContext context, String text, float x, float y, TextStyle style) {
        TextFormat format = detectTextFormat(text);
        drawText(context, text, x, y, style, format);
    }
    
    /**
     * Draw text with specified format
     */
    public static void drawText(DrawContext context, String text, float x, float y, TextStyle style, TextFormat format) {
        String processedText = preprocessText(text, format);
        RenderMode mode = determineRenderMode(style);
        
        if (mode == RenderMode.MINECRAFT) {
            drawTextMinecraft(context, processedText, x, y, style, format);
        } else {
            drawTextCustom(context, processedText, x, y, style, format);
        }
    }
    
    /**
     * Draw text using Minecraft's text renderer
     */
    private static void drawTextMinecraft(DrawContext context, String text, float x, float y, TextStyle style, TextFormat format) {
        TextRenderer textRenderer = getMinecraftTextRenderer();
        
        // Handle different formats for Minecraft renderer
        switch (format) {
            case MINECRAFT_JSON:
                drawMinecraftJson(context, text, x, y, style);
                return;
            case RICH_TEXT:
                drawRichTextMinecraft(context, text, x, y, style);
                return;
            default:
                // Handle as plain text
                break;
        }
        
        int color = style.color.toRGB();
        
        if (style.shadow) {
            // Minecraft's built-in shadow support
            context.drawTextWithShadow(textRenderer, text, (int)x, (int)y, color);
        } else {
            // Regular text without shadow
            context.drawText(textRenderer, text, (int)x, (int)y, color, false);
        }
        
        // Handle additional formatting that Minecraft doesn't support natively
        if (style.underline || style.strikethrough || style.outline) {
            drawTextDecorations(context, text, x, y, style, textRenderer);
        }
    }
    
    /**
     * Draw text using custom font rendering
     */
    private static void drawTextCustom(DrawContext context, String text, float x, float y, TextStyle style, TextFormat format) {
        Font font = getFont(style);
        if (font == null) {
            // Fallback to Minecraft renderer
            drawTextMinecraft(context, text, x, y, style, format);
            return;
        }
        
        // TODO: Implement custom text rendering using AWT font
        // This would involve:
        // 1. Rendering text to a BufferImage using AWT
        // 2. Converting to texture
        // 3. Drawing texture with proper transformations
        // 4. Handling effects like shadow, outline, etc.
        
        // For now, show what would be rendered
        System.out.println("Custom text render: '" + text + "' at " + x + "," + y + 
                          " with font " + style.fontFamily + " size " + style.size);
        
        // Fallback to Minecraft for now
        drawTextMinecraft(context, text, x, y, style, format);
    }
    
    /**
     * Draw text decorations (underline, strikethrough, outline)
     */
    private static void drawTextDecorations(DrawContext context, String text, float x, float y, 
                                          TextStyle style, TextRenderer textRenderer) {
        if (style.underline) {
            float textWidth = textRenderer.getWidth(text);
            float lineY = y + textRenderer.fontHeight - 1;
            // Draw underline using fillRect instead of drawLine
            AutreRenderer2.fillRect(context.getMatrices(), x, lineY, textWidth, 1f, style.color);
        }
        
        if (style.strikethrough) {
            float textWidth = textRenderer.getWidth(text);
            float lineY = y + textRenderer.fontHeight / 2f;
            // Draw strikethrough using fillRect instead of drawLine
            AutreRenderer2.fillRect(context.getMatrices(), x, lineY, textWidth, 1f, style.color);
        }
        
        if (style.outline) {
            // Draw text outline by drawing text in outline color at multiple offsets
            int outlineColor = style.outlineColor.toRGB();
            float offset = style.outlineWidth;
            
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    context.drawText(textRenderer, text, (int)(x + dx * offset), (int)(y + dy * offset), 
                                   outlineColor, false);
                }
            }
        }
    }
    
    /**
     * Draw rich text with formatting codes
     */
    public static void drawRichText(DrawContext context, String richText, float x, float y, TextStyle baseStyle) {
        String[] segments = parseRichText(richText);
        float currentX = x;
        
        TextStyle currentStyle = baseStyle;
        
        for (String segment : segments) {
            if (segment.startsWith("&") && segment.length() >= 2) {
                // This is a formatting code
                currentStyle = applyFormattingCode(currentStyle, segment.charAt(1));
            } else {
                // This is text to render
                drawText(context, segment, currentX, y, currentStyle);
                currentX += getTextWidth(segment, currentStyle);
            }
        }
    }
    
    /**
     * Parse rich text into segments
     */
    private static String[] parseRichText(String richText) {
        // Simple implementation - in practice you'd want more sophisticated parsing
        return richText.split("(?=&[0-9a-fklmnor])|(?<=&[0-9a-fklmnor])");
    }
    
    /**
     * Apply formatting code to create new style
     */
    private static TextStyle applyFormattingCode(TextStyle baseStyle, char code) {
        TextStyle.Builder builder = TextStyle.builder()
            .font(baseStyle.fontFamily)
            .size(baseStyle.size)
            .color(baseStyle.color)
            .renderMode(baseStyle.renderMode);
        
        switch (code) {
            case 'l': return builder.bold(true).build();
            case 'o': return builder.italic(true).build();
            case 'n': return builder.underline(true).build();
            case 'm': return builder.strikethrough(true).build();
            case 'r': return builder.build(); // Reset
            case '0': return builder.color(new AutreRenderer2.Color(0x000000)).build();
            case '1': return builder.color(new AutreRenderer2.Color(0x0000AA)).build();
            case '2': return builder.color(new AutreRenderer2.Color(0x00AA00)).build();
            case '3': return builder.color(new AutreRenderer2.Color(0x00AAAA)).build();
            case '4': return builder.color(new AutreRenderer2.Color(0xAA0000)).build();
            case '5': return builder.color(new AutreRenderer2.Color(0xAA00AA)).build();
            case '6': return builder.color(new AutreRenderer2.Color(0xFFAA00)).build();
            case '7': return builder.color(new AutreRenderer2.Color(0xAAAAAA)).build();
            case '8': return builder.color(new AutreRenderer2.Color(0x555555)).build();
            case '9': return builder.color(new AutreRenderer2.Color(0x5555FF)).build();
            case 'a': return builder.color(new AutreRenderer2.Color(0x55FF55)).build();
            case 'b': return builder.color(new AutreRenderer2.Color(0x55FFFF)).build();
            case 'c': return builder.color(new AutreRenderer2.Color(0xFF5555)).build();
            case 'd': return builder.color(new AutreRenderer2.Color(0xFF55FF)).build();
            case 'e': return builder.color(new AutreRenderer2.Color(0xFFFF55)).build();
            case 'f': return builder.color(new AutreRenderer2.Color(0xFFFFFF)).build();
            default: return baseStyle;
        }
    }
    
    /**
     * Draw wrapped text that fits within a specified width
     */
    public static void drawWrappedText(DrawContext context, String text, float x, float y, float maxWidth, TextStyle style) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        float currentY = y;
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            float lineWidth = getTextWidth(testLine, style);
            
            if (lineWidth > maxWidth && currentLine.length() > 0) {
                // Draw current line and start new line
                drawText(context, currentLine.toString(), x, currentY, style);
                currentY += getTextHeight(style) + 2f;
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        
        // Draw remaining text
        if (currentLine.length() > 0) {
            drawText(context, currentLine.toString(), x, currentY, style);
        }
    }
    
    /**
     * Draw centered text
     */
    public static void drawCenteredText(DrawContext context, String text, float centerX, float y, TextStyle style) {
        float textWidth = getTextWidth(text, style);
        drawText(context, text, centerX - textWidth / 2f, y, style);
    }
    
    /**
     * Get text width using the appropriate renderer
     */
    public static float getTextWidth(String text, TextStyle style) {
        RenderMode mode = determineRenderMode(style);
        
        if (mode == RenderMode.MINECRAFT) {
            return getMinecraftTextRenderer().getWidth(text);
        } else {
            Font font = getFont(style);
            if (font == null) {
                return getMinecraftTextRenderer().getWidth(text);
            }
            
            // Use AWT FontMetrics for custom fonts
            return (float) font.getStringBounds(text, fontRenderContext).getWidth();
        }
    }
    
    /**
     * Get text height using the appropriate renderer
     */
    public static float getTextHeight(TextStyle style) {
        RenderMode mode = determineRenderMode(style);
        
        if (mode == RenderMode.MINECRAFT) {
            return getMinecraftTextRenderer().fontHeight;
        } else {
            Font font = getFont(style);
            if (font == null) {
                return getMinecraftTextRenderer().fontHeight;
            }
            
            // Use AWT FontMetrics for custom fonts
            return (float) font.getStringBounds("Ag", fontRenderContext).getHeight();
        }
    }
    
    /**
     * Check if a custom font is available
     */
    public static boolean isCustomFontAvailable(String fontName) {
        return customFonts.containsKey(fontName);
    }
    
    /**
     * Get all available font names
     */
    public static String[] getAvailableFonts() {
        return customFonts.keySet().toArray(new String[0]);
    }
    
    /**
     * Force a specific rendering mode for a font
     */
    public static TextStyle forceMinecraftRenderer(TextStyle style) {
        return TextStyle.builder()
            .font(style.fontFamily)
            .size(style.size)
            .bold(style.bold)
            .italic(style.italic)
            .underline(style.underline)
            .strikethrough(style.strikethrough)
            .shadow(style.shadow)
            .outline(style.outline)
            .color(style.color)
            .shadowColor(style.shadowColor)
            .outlineColor(style.outlineColor)
            .shadowOffset(style.shadowOffsetX, style.shadowOffsetY)
            .outlineWidth(style.outlineWidth)
            .renderMode(RenderMode.MINECRAFT)
            .build();
    }
    
    /**
     * Force a specific rendering mode for a font
     */
    public static TextStyle forceCustomRenderer(TextStyle style) {
        return TextStyle.builder()
            .font(style.fontFamily)
            .size(style.size)
            .bold(style.bold)
            .italic(style.italic)
            .underline(style.underline)
            .strikethrough(style.strikethrough)
            .shadow(style.shadow)
            .outline(style.outline)
            .color(style.color)
            .shadowColor(style.shadowColor)
            .outlineColor(style.outlineColor)
            .shadowOffset(style.shadowOffsetX, style.shadowOffsetY)
            .outlineWidth(style.outlineWidth)
            .renderMode(RenderMode.CUSTOM)
            .build();
    }
    
    // Format-specific drawing methods
    
    /**
     * Draw Minecraft JSON text using native renderer
     */
    private static void drawMinecraftJson(DrawContext context, String jsonText, float x, float y, TextStyle style) {
        try {
            Text minecraftText = Text.Serialization.fromJson(jsonText, MinecraftClient.getInstance().world.getRegistryManager());
            if (minecraftText != null) {
                TextRenderer textRenderer = getMinecraftTextRenderer();
                if (style.shadow) {
                    context.drawTextWithShadow(textRenderer, minecraftText, (int)x, (int)y, style.color.toRGB());
                } else {
                    context.drawText(textRenderer, minecraftText, (int)x, (int)y, style.color.toRGB(), false);
                }
                return;
            }
        } catch (Exception e) {
            System.err.println("Failed to render Minecraft JSON text: " + e.getMessage());
        }
        
        // Fallback to plain text rendering
        TextRenderer textRenderer = getMinecraftTextRenderer();
        int color = style.color.toRGB();
        if (style.shadow) {
            context.drawTextWithShadow(textRenderer, jsonText, (int)x, (int)y, color);
        } else {
            context.drawText(textRenderer, jsonText, (int)x, (int)y, color, false);
        }
    }
    
    /**
     * Draw rich text with formatting codes using Minecraft renderer
     */
    private static void drawRichTextMinecraft(DrawContext context, String richText, float x, float y, TextStyle baseStyle) {
        String[] segments = parseRichText(richText);
        float currentX = x;
        TextRenderer textRenderer = getMinecraftTextRenderer();
        
        TextStyle currentStyle = baseStyle;
        
        for (String segment : segments) {
            if (segment.startsWith("&") && segment.length() >= 2) {
                // This is a formatting code
                currentStyle = applyFormattingCode(currentStyle, segment.charAt(1));
            } else if (!segment.isEmpty()) {
                // This is text to render
                int color = currentStyle.color.toRGB();
                
                if (currentStyle.shadow) {
                    context.drawTextWithShadow(textRenderer, segment, (int)currentX, (int)y, color);
                } else {
                    context.drawText(textRenderer, segment, (int)currentX, (int)y, color, false);
                }
                
                currentX += textRenderer.getWidth(segment);
                
                // Handle decorations
                if (currentStyle.underline || currentStyle.strikethrough) {
                    drawTextDecorationsSegment(context, segment, currentX - textRenderer.getWidth(segment), y, currentStyle, textRenderer);
                }
            }
        }
    }
    
    /**
     * Draw text decorations for a specific segment
     */
    private static void drawTextDecorationsSegment(DrawContext context, String text, float x, float y, 
                                                  TextStyle style, TextRenderer textRenderer) {
        if (style.underline) {
            float textWidth = textRenderer.getWidth(text);
            float lineY = y + textRenderer.fontHeight - 1;
            AutreRenderer2.fillRect(context.getMatrices(), x, lineY, textWidth, 1f, style.color);
        }
        
        if (style.strikethrough) {
            float textWidth = textRenderer.getWidth(text);
            float lineY = y + textRenderer.fontHeight / 2f;
            AutreRenderer2.fillRect(context.getMatrices(), x, lineY, textWidth, 1f, style.color);
        }
    }
    
    /**
     * Draw XFT (XMLFormattedText) directly
     */
    public static void drawXFT(DrawContext context, String xftText, float x, float y, TextStyle style) {
        drawText(context, xftText, x, y, style, TextFormat.XFT);
    }
    
    /**
     * Draw LaTeX text directly
     */
    public static void drawLaTeX(DrawContext context, String latexText, float x, float y, TextStyle style) {
        drawText(context, latexText, x, y, style, TextFormat.LATEX);
    }
    
    /**
     * Draw Minecraft JSON text directly
     */
    public static void drawMinecraftJSON(DrawContext context, String jsonText, float x, float y, TextStyle style) {
        drawText(context, jsonText, x, y, style, TextFormat.MINECRAFT_JSON);
    }
}