package star.sequoia2.autre.render.text;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unified text and emoji renderer that automatically handles both text and emojis
 * Provides seamless switching between text rendering and emoji rendering
 */
public class AutreTextRenderer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Emoji detection patterns
    private static final Pattern UNICODE_EMOJI_PATTERN = Pattern.compile(
        "[\\x{1F600}-\\x{1F64F}]|" +  // Emoticons
        "[\\x{1F300}-\\x{1F5FF}]|" +  // Misc Symbols
        "[\\x{1F680}-\\x{1F6FF}]|" +  // Transport
        "[\\x{1F1E0}-\\x{1F1FF}]|" +  // Flags (iOS)
        "[\\x{2600}-\\x{26FF}]|" +    // Misc symbols
        "[\\x{2700}-\\x{27BF}]"       // Dingbats
    );
    
    private static final Pattern SHORTCODE_PATTERN = Pattern.compile(":([a-zA-Z0-9_+-]+):");
    
    // Text segment types
    public enum SegmentType {
        TEXT,
        UNICODE_EMOJI,
        SHORTCODE_EMOJI
    }
    
    // Text segment for mixed content
    public static class TextSegment {
        public final SegmentType type;
        public final String content;
        public final float width;
        public final float height;
        
        public TextSegment(SegmentType type, String content, float width, float height) {
            this.type = type;
            this.content = content;
            this.width = width;
            this.height = height;
        }
    }
    
    // Text style definition (matches AutreTextRenderer2 for compatibility)
    public static class TextStyle {
        public static final TextStyle DEFAULT = builder().build();
        
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
            
            public TextStyle build() {
                return new TextStyle(this);
            }
        }
    }
    
    /**
     * Main rendering function that handles mixed text and emoji content
     */
    public static void drawText(DrawContext context, String text, float x, float y, TextStyle style) {
        drawText(context, text, x, y, style, false);
    }
    
    public static void drawText(DrawContext context, String text, float x, float y, TextStyle style, boolean shadow) {
        if (text == null || text.isEmpty()) return;
        
        List<TextSegment> segments = parseTextSegments(text, style);
        renderSegments(context, segments, x, y, style, shadow);
    }
    
    /**
     * Get the width of mixed text and emoji content
     */
    public static float getTextWidth(String text, TextStyle style) {
        if (text == null || text.isEmpty()) return 0f;
        
        List<TextSegment> segments = parseTextSegments(text, style);
        float totalWidth = 0f;
        for (TextSegment segment : segments) {
            totalWidth += segment.width;
        }
        return totalWidth;
    }
    
    /**
     * Get the height of mixed text and emoji content
     */
    public static float getTextHeight(String text, TextStyle style) {
        if (text == null || text.isEmpty()) return 0f;
        
        List<TextSegment> segments = parseTextSegments(text, style);
        float maxHeight = 0f;
        for (TextSegment segment : segments) {
            maxHeight = Math.max(maxHeight, segment.height);
        }
        return maxHeight;
    }
    
    /**
     * Parse text into segments of text and emojis
     */
    private static List<TextSegment> parseTextSegments(String text, TextStyle style) {
        List<TextSegment> segments = new ArrayList<>();
        
        // Find all emoji positions
        List<EmojiMatch> emojiMatches = new ArrayList<>();
        
        // Find Unicode emojis
        Matcher unicodeMatcher = UNICODE_EMOJI_PATTERN.matcher(text);
        while (unicodeMatcher.find()) {
            emojiMatches.add(new EmojiMatch(
                unicodeMatcher.start(), 
                unicodeMatcher.end(), 
                unicodeMatcher.group(), 
                SegmentType.UNICODE_EMOJI
            ));
        }
        
        // Find shortcode emojis
        Matcher shortcodeMatcher = SHORTCODE_PATTERN.matcher(text);
        while (shortcodeMatcher.find()) {
            emojiMatches.add(new EmojiMatch(
                shortcodeMatcher.start(), 
                shortcodeMatcher.end(), 
                shortcodeMatcher.group(), 
                SegmentType.SHORTCODE_EMOJI
            ));
        }
        
        // Sort matches by position
        emojiMatches.sort(Comparator.comparingInt(m -> m.start));
        
        // Create segments
        int lastEnd = 0;
        for (EmojiMatch match : emojiMatches) {
            // Add text segment before emoji
            if (match.start > lastEnd) {
                String textPart = text.substring(lastEnd, match.start);
                if (!textPart.isEmpty()) {
                    float width = mc.textRenderer.getWidth(textPart) * (style.size / 9f);
                    float height = mc.textRenderer.fontHeight * (style.size / 9f);
                    segments.add(new TextSegment(SegmentType.TEXT, textPart, width, height));
                }
            }
            
            // Add emoji segment
            float emojiSize = style.size; // Emoji size matches font size
            segments.add(new TextSegment(match.type, match.content, emojiSize, emojiSize));
            
            lastEnd = match.end;
        }
        
        // Add remaining text
        if (lastEnd < text.length()) {
            String textPart = text.substring(lastEnd);
            if (!textPart.isEmpty()) {
                float width = mc.textRenderer.getWidth(textPart) * (style.size / 9f);
                float height = mc.textRenderer.fontHeight * (style.size / 9f);
                segments.add(new TextSegment(SegmentType.TEXT, textPart, width, height));
            }
        }
        
        // If no emojis found, add the entire text as one segment
        if (segments.isEmpty() && !text.isEmpty()) {
            float width = mc.textRenderer.getWidth(text) * (style.size / 9f);
            float height = mc.textRenderer.fontHeight * (style.size / 9f);
            segments.add(new TextSegment(SegmentType.TEXT, text, width, height));
        }
        
        return segments;
    }
    
    /**
     * Render all segments
     */
    private static void renderSegments(DrawContext context, List<TextSegment> segments, 
                                     float x, float y, TextStyle style, boolean shadow) {
        float currentX = x;
        
        for (TextSegment segment : segments) {
            switch (segment.type) {
                case TEXT:
                    renderTextSegment(context, segment.content, currentX, y, style, shadow);
                    break;
                case UNICODE_EMOJI:
                case SHORTCODE_EMOJI:
                    renderEmojiSegment(context, segment.content, currentX, y, style);
                    break;
            }
            currentX += segment.width;
        }
    }
    
    /**
     * Render a text segment
     */
    private static void renderTextSegment(DrawContext context, String text, float x, float y, 
                                        TextStyle style, boolean shadow) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        
        // Apply scaling based on font size
        float scale = style.size / 9f; // 9 is default Minecraft font size
        matrices.scale(scale, scale, 1f);
        
        int color = style.color.getRGBA();
        
        // Render shadow if enabled
        if (shadow || style.shadow) {
            int shadowColor = style.shadowColor.getRGBA();
            context.drawText(mc.textRenderer, text, 
                (int)((x + style.shadowOffsetX) / scale), 
                (int)((y + style.shadowOffsetY) / scale), 
                shadowColor, false);
        }
        
        // Render main text
        context.drawText(mc.textRenderer, text, 
            (int)(x / scale), 
            (int)(y / scale), 
            color, false);
        
        matrices.pop();
    }
    
    /**
     * Render an emoji segment
     */
    private static void renderEmojiSegment(DrawContext context, String emoji, float x, float y, TextStyle style) {
        // For now, render emoji as text using Minecraft's renderer
        // In the future, this would integrate with tweemoji
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        
        float scale = style.size / 9f;
        matrices.scale(scale, scale, 1f);
        
        int color = style.color.getRGBA();
        
        // Convert shortcode to unicode if needed
        String emojiText = emoji;
        if (emoji.startsWith(":") && emoji.endsWith(":")) {
            // This would be replaced with actual shortcode to unicode conversion
            emojiText = convertShortcodeToUnicode(emoji);
        }
        
        context.drawText(mc.textRenderer, emojiText, 
            (int)(x / scale), 
            (int)(y / scale), 
            color, false);
        
        matrices.pop();
    }
    
    /**
     * Convert shortcode to unicode (placeholder implementation)
     */
    private static String convertShortcodeToUnicode(String shortcode) {
        // Common emoji mappings
        Map<String, String> commonEmojis = new HashMap<>();
        commonEmojis.put(":smile:", "😄");
        commonEmojis.put(":heart:", "❤️");
        commonEmojis.put(":thumbsup:", "👍");
        commonEmojis.put(":fire:", "🔥");
        commonEmojis.put(":star:", "⭐");
        commonEmojis.put(":wave:", "👋");
        commonEmojis.put(":crying:", "😢");
        commonEmojis.put(":laughing:", "😆");
        commonEmojis.put(":wink:", "😉");
        commonEmojis.put(":cool:", "😎");
        
        return commonEmojis.getOrDefault(shortcode, shortcode);
    }
    
    /**
     * Helper class for emoji matching
     */
    private static class EmojiMatch {
        final int start;
        final int end;
        final String content;
        final SegmentType type;
        
        EmojiMatch(int start, int end, String content, SegmentType type) {
            this.start = start;
            this.end = end;
            this.content = content;
            this.type = type;
        }
    }
    
    // Utility methods for backward compatibility
    
    /**
     * Simple text rendering without style
     */
    public static void drawText(DrawContext context, String text, float x, float y, AutreRenderer2.Color color) {
        TextStyle style = TextStyle.builder().color(color).build();
        drawText(context, text, x, y, style);
    }
    
    /**
     * Text rendering with shadow
     */
    public static void drawText(DrawContext context, String text, float x, float y, AutreRenderer2.Color color, boolean shadow) {
        TextStyle style = TextStyle.builder().color(color).shadow(shadow).build();
        drawText(context, text, x, y, style, shadow);
    }
    
    /**
     * Get simple text width
     */
    public static float getTextWidth(String text) {
        return getTextWidth(text, TextStyle.builder().build());
    }
    
    /**
     * Get simple text height  
     */
    public static float getTextHeight(String text) {
        return getTextHeight(text, TextStyle.builder().build());
    }
    
    /**
     * Get text height for a given style (returns font height for the style)
     */
    public static float getTextHeight(TextStyle style) {
        return mc.textRenderer.fontHeight * (style.size / 9f);
    }
    
    // Font loading methods for compatibility with AutreFontManager
    
    /**
     * Load a custom font from file (placeholder implementation)
     */
    public static boolean loadCustomFont(String fontName, java.io.File fontFile) {
        // TODO: Implement custom font loading from file
        System.out.println("Loading custom font: " + fontName + " from file: " + fontFile.getPath());
        return true; // Return true for now to avoid breaking existing code
    }
    
    /**
     * Load a custom font from resource path (placeholder implementation)
     */
    public static boolean loadCustomFont(String fontName, String resourcePath) {
        // TODO: Implement custom font loading from resource
        System.out.println("Loading custom font: " + fontName + " from resource: " + resourcePath);
        return true; // Return true for now to avoid breaking existing code
    }
}