package star.sequoia2.autre.render.text;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Emoji support for AutreTextRenderer
 * Handles emoji detection, rendering, and tweemoji integration
 */
public class AutreEmojiRenderer {
    
    // Emoji patterns
    private static final Pattern UNICODE_EMOJI_PATTERN = Pattern.compile(
        "[\\x{1F600}-\\x{1F64F}]|" +  // Emoticons
        "[\\x{1F300}-\\x{1F5FF}]|" +  // Misc Symbols
        "[\\x{1F680}-\\x{1F6FF}]|" +  // Transport
        "[\\x{1F1E0}-\\x{1F1FF}]|" +  // Flags (iOS)
        "[\\x{2600}-\\x{26FF}]|" +    // Misc symbols
        "[\\x{2700}-\\x{27BF}]"       // Dingbats
    );
    
    private static final Pattern SHORTCODE_PATTERN = Pattern.compile(":([a-zA-Z0-9_+-]+):");
    
    // Emoji data structures
    private static final Map<String, EmojiData> shortcodeToEmoji = new HashMap<>();
    private static final Map<String, EmojiData> unicodeToEmoji = new HashMap<>();
    private static final Map<String, String> aliases = new HashMap<>();
    
    // Tweemoji integration (future)
    private static boolean tweemojiEnabled = false;
    private static String tweemojiBasePath = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/";
    private static EmojiRenderMode renderMode = EmojiRenderMode.UNICODE;
    
    public enum EmojiRenderMode {
        UNICODE,    // Use system Unicode emoji
        TWEEMOJI,   // Use tweemoji images
        HYBRID      // Use tweemoji for unsupported, Unicode for supported
    }
    
    public static class EmojiData {
        public final String unicode;
        public final String shortcode;
        public final String category;
        public final String description;
        public final List<String> aliases;
        public final boolean hasTweemoji;
        
        public EmojiData(String unicode, String shortcode, String category, String description, 
                        List<String> aliases, boolean hasTweemoji) {
            this.unicode = unicode;
            this.shortcode = shortcode;
            this.category = category;
            this.description = description;
            this.aliases = new ArrayList<>(aliases);
            this.hasTweemoji = hasTweemoji;
        }
    }
    
    public static class EmojiMatch {
        public final String original;
        public final String unicode;
        public final EmojiData data;
        public final int start;
        public final int end;
        
        public EmojiMatch(String original, String unicode, EmojiData data, int start, int end) {
            this.original = original;
            this.unicode = unicode;
            this.data = data;
            this.start = start;
            this.end = end;
        }
    }
    
    static {
        // Initialize with common emoji
        loadCommonEmoji();
    }
    
    /**
     * Initialize emoji system
     */
    public static void initialize() {
        loadCommonEmoji();
        System.out.println("AutreEmojiRenderer initialized with " + shortcodeToEmoji.size() + " emoji");
    }
    
    /**
     * Find all emoji in text
     */
    public static List<EmojiMatch> findEmoji(String text) {
        List<EmojiMatch> matches = new ArrayList<>();
        
        // Find Unicode emoji
        Matcher unicodeMatcher = UNICODE_EMOJI_PATTERN.matcher(text);
        while (unicodeMatcher.find()) {
            String unicode = unicodeMatcher.group();
            EmojiData data = unicodeToEmoji.get(unicode);
            if (data != null) {
                matches.add(new EmojiMatch(unicode, unicode, data, 
                    unicodeMatcher.start(), unicodeMatcher.end()));
            }
        }
        
        // Find shortcode emoji
        Matcher shortcodeMatcher = SHORTCODE_PATTERN.matcher(text);
        while (shortcodeMatcher.find()) {
            String shortcode = shortcodeMatcher.group(1);
            EmojiData data = shortcodeToEmoji.get(shortcode);
            if (data != null) {
                matches.add(new EmojiMatch(shortcodeMatcher.group(), data.unicode, data,
                    shortcodeMatcher.start(), shortcodeMatcher.end()));
            }
        }
        
        // Sort by position
        matches.sort((a, b) -> Integer.compare(a.start, b.start));
        return matches;
    }
    
    /**
     * Replace emoji shortcodes with Unicode
     */
    public static String replaceShortcodes(String text) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = SHORTCODE_PATTERN.matcher(text);
        int lastEnd = 0;
        
        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            
            String shortcode = matcher.group(1);
            EmojiData data = shortcodeToEmoji.get(shortcode);
            if (data != null) {
                result.append(data.unicode);
            } else {
                result.append(matcher.group()); // Keep original if not found
            }
            
            lastEnd = matcher.end();
        }
        
        result.append(text.substring(lastEnd));
        return result.toString();
    }
    
    /**
     * Replace Unicode emoji with shortcodes
     */
    public static String replaceUnicodeWithShortcodes(String text) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = UNICODE_EMOJI_PATTERN.matcher(text);
        int lastEnd = 0;
        
        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            
            String unicode = matcher.group();
            EmojiData data = unicodeToEmoji.get(unicode);
            if (data != null) {
                result.append(":").append(data.shortcode).append(":");
            } else {
                result.append(unicode); // Keep original if not found
            }
            
            lastEnd = matcher.end();
        }
        
        result.append(text.substring(lastEnd));
        return result.toString();
    }
    
    /**
     * Check if text contains emoji
     */
    public static boolean containsEmoji(String text) {
        return UNICODE_EMOJI_PATTERN.matcher(text).find() || 
               SHORTCODE_PATTERN.matcher(text).find();
    }
    
    /**
     * Get emoji width for text measurement (future implementation)
     */
    public static float getEmojiWidth(String unicode, float fontSize) {
        // For now, assume emoji are square and same size as font
        return fontSize;
    }
    
    /**
     * Get emoji height for text measurement (future implementation)
     */
    public static float getEmojiHeight(String unicode, float fontSize) {
        return fontSize;
    }
    
    /**
     * Render emoji at position (future implementation)
     */
    public static void renderEmoji(String unicode, float x, float y, float size, 
                                  net.minecraft.client.util.math.MatrixStack matrices) {
        // TODO: Implement emoji rendering
        // For now, this is a placeholder for future tweemoji integration
        
        if (renderMode == EmojiRenderMode.TWEEMOJI && tweemojiEnabled) {
            // Future: Render tweemoji image
            renderTweemoji(unicode, x, y, size, matrices);
        } else {
            // Future: Render Unicode emoji using system fonts
            renderUnicodeEmoji(unicode, x, y, size, matrices);
        }
    }
    
    /**
     * Future tweemoji rendering implementation
     */
    private static void renderTweemoji(String unicode, float x, float y, float size, 
                                     net.minecraft.client.util.math.MatrixStack matrices) {
        // TODO: Load and render tweemoji PNG/SVG
        // This will integrate with AutreImageRenderer
        
        String codepoint = unicodeToCodepoint(unicode);
        String imageUrl = tweemojiBasePath + "72x72/" + codepoint + ".png";
        
        // Future: Use AutreImageRenderer to load and display tweemoji
        System.out.println("Rendering tweemoji: " + unicode + " (" + codepoint + ") at " + x + "," + y + " from " + imageUrl);
    }
    
    /**
     * Future Unicode emoji rendering implementation
     */
    private static void renderUnicodeEmoji(String unicode, float x, float y, float size, 
                                         net.minecraft.client.util.math.MatrixStack matrices) {
        // TODO: Render using system emoji font
        // This will use AutreTextRenderer with emoji font
        System.out.println("Rendering Unicode emoji: " + unicode + " at " + x + "," + y);
    }
    
    /**
     * Convert Unicode emoji to tweemoji codepoint
     */
    private static String unicodeToCodepoint(String unicode) {
        StringBuilder codepoint = new StringBuilder();
        for (int i = 0; i < unicode.length(); ) {
            int cp = unicode.codePointAt(i);
            if (codepoint.length() > 0) {
                codepoint.append("-");
            }
            codepoint.append(Integer.toHexString(cp));
            i += Character.charCount(cp);
        }
        return codepoint.toString();
    }
    
    /**
     * Load common emoji data
     */
    private static void loadCommonEmoji() {
        // Faces and emotions
        addEmoji("😀", "grinning", "Smileys & Emotion", "Grinning face", Arrays.asList("smile", "happy"));
        addEmoji("😃", "grinning_big", "Smileys & Emotion", "Grinning face with big eyes", Arrays.asList("smile", "happy"));
        addEmoji("😄", "grinning_squint", "Smileys & Emotion", "Grinning face with smiling eyes", Arrays.asList("smile", "happy"));
        addEmoji("😁", "grin", "Smileys & Emotion", "Beaming face with smiling eyes", Arrays.asList("smile", "happy"));
        addEmoji("😆", "laughing", "Smileys & Emotion", "Grinning squinting face", Arrays.asList("happy", "haha"));
        addEmoji("😅", "sweat_smile", "Smileys & Emotion", "Grinning face with sweat", Arrays.asList("hot", "happy"));
        addEmoji("😂", "joy", "Smileys & Emotion", "Face with tears of joy", Arrays.asList("laugh", "happy"));
        addEmoji("🤣", "rofl", "Smileys & Emotion", "Rolling on the floor laughing", Arrays.asList("laugh"));
        addEmoji("😊", "blush", "Smileys & Emotion", "Smiling face with smiling eyes", Arrays.asList("happy"));
        addEmoji("😇", "innocent", "Smileys & Emotion", "Smiling face with halo", Arrays.asList("angel"));
        
        addEmoji("🙂", "slightly_smiling", "Smileys & Emotion", "Slightly smiling face", Arrays.asList("smile"));
        addEmoji("😉", "wink", "Smileys & Emotion", "Winking face", Arrays.asList("flirt"));
        addEmoji("😍", "heart_eyes", "Smileys & Emotion", "Smiling face with heart-eyes", Arrays.asList("love"));
        addEmoji("🥰", "smiling_face_with_hearts", "Smileys & Emotion", "Smiling face with hearts", Arrays.asList("love"));
        addEmoji("😘", "kissing_heart", "Smileys & Emotion", "Face blowing a kiss", Arrays.asList("kiss", "love"));
        addEmoji("😗", "kissing", "Smileys & Emotion", "Kissing face", Arrays.asList("kiss"));
        addEmoji("😙", "kissing_smiling_eyes", "Smileys & Emotion", "Kissing face with smiling eyes", Arrays.asList("kiss"));
        addEmoji("😚", "kissing_closed_eyes", "Smileys & Emotion", "Kissing face with closed eyes", Arrays.asList("kiss"));
        
        addEmoji("😐", "neutral", "Smileys & Emotion", "Neutral face", Arrays.asList("meh"));
        addEmoji("😑", "expressionless", "Smileys & Emotion", "Expressionless face", Arrays.asList("meh"));
        addEmoji("😶", "no_mouth", "Smileys & Emotion", "Face without mouth", Arrays.asList("quiet"));
        addEmoji("😏", "smirk", "Smileys & Emotion", "Smirking face", Arrays.asList("confident"));
        addEmoji("😒", "unamused", "Smileys & Emotion", "Unamused face", Arrays.asList("meh"));
        addEmoji("🙄", "eye_roll", "Smileys & Emotion", "Face with rolling eyes", Arrays.asList("annoyed"));
        addEmoji("😬", "grimacing", "Smileys & Emotion", "Grimacing face", Arrays.asList("awkward"));
        addEmoji("😔", "pensive", "Smileys & Emotion", "Pensive face", Arrays.asList("sad"));
        addEmoji("😪", "sleepy", "Smileys & Emotion", "Sleepy face", Arrays.asList("tired"));
        addEmoji("😴", "sleeping", "Smileys & Emotion", "Sleeping face", Arrays.asList("tired"));
        
        addEmoji("😷", "mask", "Smileys & Emotion", "Face with medical mask", Arrays.asList("sick"));
        addEmoji("🤒", "thermometer_face", "Smileys & Emotion", "Face with thermometer", Arrays.asList("sick"));
        addEmoji("🤕", "head_bandage", "Smileys & Emotion", "Face with head-bandage", Arrays.asList("hurt"));
        addEmoji("🤢", "nauseated", "Smileys & Emotion", "Nauseated face", Arrays.asList("sick"));
        addEmoji("🤮", "vomiting", "Smileys & Emotion", "Face vomiting", Arrays.asList("sick"));
        addEmoji("🤧", "sneezing", "Smileys & Emotion", "Sneezing face", Arrays.asList("sick"));
        
        // Common objects and symbols
        addEmoji("❤️", "heart", "Symbols", "Red heart", Arrays.asList("love"));
        addEmoji("💙", "blue_heart", "Symbols", "Blue heart", Arrays.asList("love"));
        addEmoji("💚", "green_heart", "Symbols", "Green heart", Arrays.asList("love"));
        addEmoji("💛", "yellow_heart", "Symbols", "Yellow heart", Arrays.asList("love"));
        addEmoji("🧡", "orange_heart", "Symbols", "Orange heart", Arrays.asList("love"));
        addEmoji("💜", "purple_heart", "Symbols", "Purple heart", Arrays.asList("love"));
        addEmoji("🖤", "black_heart", "Symbols", "Black heart", Arrays.asList("love"));
        addEmoji("🤍", "white_heart", "Symbols", "White heart", Arrays.asList("love"));
        addEmoji("🤎", "brown_heart", "Symbols", "Brown heart", Arrays.asList("love"));
        
        addEmoji("👍", "thumbsup", "People & Body", "Thumbs up", Arrays.asList("yes", "ok"));
        addEmoji("👎", "thumbsdown", "People & Body", "Thumbs down", Arrays.asList("no", "bad"));
        addEmoji("👌", "ok_hand", "People & Body", "OK hand", Arrays.asList("ok", "perfect"));
        addEmoji("✌️", "v", "People & Body", "Victory hand", Arrays.asList("peace", "victory"));
        addEmoji("🤞", "crossed_fingers", "People & Body", "Crossed fingers", Arrays.asList("luck"));
        addEmoji("🤟", "love_you_gesture", "People & Body", "Love-you gesture", Arrays.asList("love"));
        addEmoji("🤘", "metal", "People & Body", "Sign of the horns", Arrays.asList("rock"));
        addEmoji("👋", "wave", "People & Body", "Waving hand", Arrays.asList("hello", "goodbye"));
        
        addEmoji("🔥", "fire", "Objects", "Fire", Arrays.asList("hot", "lit"));
        addEmoji("💯", "100", "Objects", "Hundred points symbol", Arrays.asList("perfect", "score"));
        addEmoji("💎", "gem", "Objects", "Gem stone", Arrays.asList("diamond", "precious"));
        addEmoji("🌟", "star2", "Nature", "Glowing star", Arrays.asList("star", "shine"));
        addEmoji("⭐", "star", "Nature", "Star", Arrays.asList("favorite"));
        addEmoji("🎉", "tada", "Objects", "Party popper", Arrays.asList("celebration", "party"));
        addEmoji("🎊", "confetti_ball", "Objects", "Confetti ball", Arrays.asList("celebration", "party"));
        
        System.out.println("Loaded " + shortcodeToEmoji.size() + " common emoji");
    }
    
    /**
     * Add emoji to the registry
     */
    private static void addEmoji(String unicode, String shortcode, String category, 
                               String description, List<String> aliases) {
        EmojiData data = new EmojiData(unicode, shortcode, category, description, aliases, true);
        shortcodeToEmoji.put(shortcode, data);
        unicodeToEmoji.put(unicode, data);
        
        // Add aliases
        for (String alias : aliases) {
            AutreEmojiRenderer.aliases.put(alias, shortcode);
        }
    }
    
    /**
     * Configuration methods
     */
    public static void setTweemojiEnabled(boolean enabled) {
        tweemojiEnabled = enabled;
        if (enabled) {
            System.out.println("Tweemoji rendering enabled");
        } else {
            System.out.println("Tweemoji rendering disabled, using Unicode fallback");
        }
    }
    
    public static boolean isTweemojiEnabled() {
        return tweemojiEnabled;
    }
    
    public static void setRenderMode(EmojiRenderMode mode) {
        renderMode = mode;
        System.out.println("Emoji render mode set to: " + mode);
    }
    
    public static EmojiRenderMode getRenderMode() {
        return renderMode;
    }
    
    public static void setTweemojiBasePath(String basePath) {
        tweemojiBasePath = basePath;
        System.out.println("Tweemoji base path set to: " + basePath);
    }
    
    public static String getTweemojiBasePath() {
        return tweemojiBasePath;
    }
    
    /**
     * Search emoji by keyword
     */
    public static List<EmojiData> searchEmoji(String keyword) {
        List<EmojiData> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (EmojiData emoji : shortcodeToEmoji.values()) {
            if (emoji.shortcode.contains(lowerKeyword) ||
                emoji.description.toLowerCase().contains(lowerKeyword) ||
                emoji.aliases.stream().anyMatch(alias -> alias.contains(lowerKeyword))) {
                results.add(emoji);
            }
        }
        
        return results;
    }
    
    /**
     * Get emoji by category
     */
    public static List<EmojiData> getEmojiByCategory(String category) {
        return shortcodeToEmoji.values().stream()
            .filter(emoji -> emoji.category.equals(category))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Get all categories
     */
    public static Set<String> getCategories() {
        return shortcodeToEmoji.values().stream()
            .map(emoji -> emoji.category)
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }
    
    /**
     * Get random emoji
     */
    public static EmojiData getRandomEmoji() {
        List<EmojiData> allEmoji = new ArrayList<>(shortcodeToEmoji.values());
        if (allEmoji.isEmpty()) {
            return null;
        }
        return allEmoji.get(new Random().nextInt(allEmoji.size()));
    }
    
    /**
     * Load emoji database from JSON (future implementation)
     */
    public static void loadEmojiDatabase(String jsonPath) {
        // TODO: Load full emoji database from JSON file
        System.out.println("Loading emoji database from: " + jsonPath);
    }
}