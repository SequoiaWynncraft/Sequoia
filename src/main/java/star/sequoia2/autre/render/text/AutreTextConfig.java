package star.sequoia2.autre.render.text;

import java.util.HashMap;
import java.util.Map;
import star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle;

/**
 * Configuration for AutreTextRenderer
 * Manages global settings and performance options
 */
public class AutreTextConfig {
    
    // Global settings
    private static boolean antiAliasing = true;
    private static boolean fractionalMetrics = true;
    private static float globalScale = 1.0f;
    private static int maxCacheSize = 1000;
    private static boolean enableShadows = true;
    private static boolean enableOutlines = true;
    private static boolean enableAnimations = true;
    private static boolean enableRichText = true;
    
    // Performance settings
    private static boolean enableFontCache = true;
    private static boolean enableTextureAtlas = true;
    private static boolean enableBatching = true;
    private static int batchSize = 100;
    private static float cacheEvictionThreshold = 0.8f;
    
    // Rendering hints
    private static final Map<String, Object> renderingHints = new HashMap<>();
    
    // Default styles
    private static TextStyle defaultStyle;
    private static TextStyle debugStyle;
    private static TextStyle errorStyle;
    
    static {
        // Initialize default styles
        defaultStyle = TextStyle.builder()
            .font("minecraft")
            .size(9f)
            .color(star.sequoia2.autre.render.AutreRenderer2.Color.WHITE)
            .build();
            
        debugStyle = TextStyle.builder()
            .font("minecraft")
            .size(8f)
            .color(new star.sequoia2.autre.render.AutreRenderer2.Color(0xFF00FF00))
            .shadow(true)
            .build();
            
        errorStyle = TextStyle.builder()
            .font("minecraft")
            .size(9f)
            .color(new star.sequoia2.autre.render.AutreRenderer2.Color(0xFFFF0000))
            .bold(true)
            .shadow(true)
            .build();
            
        // Set default rendering hints
        renderingHints.put("TEXT_ANTIALIASING", "ON");
        renderingHints.put("FRACTIONAL_METRICS", "ON");
        renderingHints.put("RENDERING", "QUALITY");
    }
    
    // Getters and setters
    
    public static boolean isAntiAliasing() {
        return antiAliasing;
    }
    
    public static void setAntiAliasing(boolean antiAliasing) {
        AutreTextConfig.antiAliasing = antiAliasing;
    }
    
    public static boolean isFractionalMetrics() {
        return fractionalMetrics;
    }
    
    public static void setFractionalMetrics(boolean fractionalMetrics) {
        AutreTextConfig.fractionalMetrics = fractionalMetrics;
    }
    
    public static float getGlobalScale() {
        return globalScale;
    }
    
    public static void setGlobalScale(float globalScale) {
        AutreTextConfig.globalScale = Math.max(0.1f, Math.min(5.0f, globalScale));
    }
    
    public static int getMaxCacheSize() {
        return maxCacheSize;
    }
    
    public static void setMaxCacheSize(int maxCacheSize) {
        AutreTextConfig.maxCacheSize = Math.max(100, maxCacheSize);
    }
    
    public static boolean isEnableShadows() {
        return enableShadows;
    }
    
    public static void setEnableShadows(boolean enableShadows) {
        AutreTextConfig.enableShadows = enableShadows;
    }
    
    public static boolean isEnableOutlines() {
        return enableOutlines;
    }
    
    public static void setEnableOutlines(boolean enableOutlines) {
        AutreTextConfig.enableOutlines = enableOutlines;
    }
    
    public static boolean isEnableAnimations() {
        return enableAnimations;
    }
    
    public static void setEnableAnimations(boolean enableAnimations) {
        AutreTextConfig.enableAnimations = enableAnimations;
    }
    
    public static boolean isEnableRichText() {
        return enableRichText;
    }
    
    public static void setEnableRichText(boolean enableRichText) {
        AutreTextConfig.enableRichText = enableRichText;
    }
    
    public static boolean isEnableFontCache() {
        return enableFontCache;
    }
    
    public static void setEnableFontCache(boolean enableFontCache) {
        AutreTextConfig.enableFontCache = enableFontCache;
    }
    
    public static boolean isEnableTextureAtlas() {
        return enableTextureAtlas;
    }
    
    public static void setEnableTextureAtlas(boolean enableTextureAtlas) {
        AutreTextConfig.enableTextureAtlas = enableTextureAtlas;
    }
    
    public static boolean isEnableBatching() {
        return enableBatching;
    }
    
    public static void setEnableBatching(boolean enableBatching) {
        AutreTextConfig.enableBatching = enableBatching;
    }
    
    public static int getBatchSize() {
        return batchSize;
    }
    
    public static void setBatchSize(int batchSize) {
        AutreTextConfig.batchSize = Math.max(10, Math.min(1000, batchSize));
    }
    
    public static float getCacheEvictionThreshold() {
        return cacheEvictionThreshold;
    }
    
    public static void setCacheEvictionThreshold(float cacheEvictionThreshold) {
        AutreTextConfig.cacheEvictionThreshold = Math.max(0.5f, Math.min(0.95f, cacheEvictionThreshold));
    }
    
    public static Map<String, Object> getRenderingHints() {
        return new HashMap<>(renderingHints);
    }
    
    public static void setRenderingHint(String key, Object value) {
        renderingHints.put(key, value);
    }
    
    public static TextStyle getDefaultStyle() {
        return defaultStyle;
    }
    
    public static void setDefaultStyle(TextStyle defaultStyle) {
        AutreTextConfig.defaultStyle = defaultStyle;
    }
    
    public static TextStyle getDebugStyle() {
        return debugStyle;
    }
    
    public static void setDebugStyle(TextStyle debugStyle) {
        AutreTextConfig.debugStyle = debugStyle;
    }
    
    public static TextStyle getErrorStyle() {
        return errorStyle;
    }
    
    public static void setErrorStyle(TextStyle errorStyle) {
        AutreTextConfig.errorStyle = errorStyle;
    }
    
    /**
     * Performance presets
     */
    public static class PerformancePreset {
        
        public static void setHighQuality() {
            setAntiAliasing(true);
            setFractionalMetrics(true);
            setEnableShadows(true);
            setEnableOutlines(true);
            setEnableAnimations(true);
            setEnableBatching(true);
            setEnableTextureAtlas(true);
            setBatchSize(50);
            setMaxCacheSize(2000);
            System.out.println("Text renderer set to High Quality mode");
        }
        
        public static void setBalanced() {
            setAntiAliasing(true);
            setFractionalMetrics(true);
            setEnableShadows(true);
            setEnableOutlines(false);
            setEnableAnimations(true);
            setEnableBatching(true);
            setEnableTextureAtlas(true);
            setBatchSize(100);
            setMaxCacheSize(1000);
            System.out.println("Text renderer set to Balanced mode");
        }
        
        public static void setPerformance() {
            setAntiAliasing(false);
            setFractionalMetrics(false);
            setEnableShadows(false);
            setEnableOutlines(false);
            setEnableAnimations(false);
            setEnableBatching(true);
            setEnableTextureAtlas(true);
            setBatchSize(200);
            setMaxCacheSize(500);
            System.out.println("Text renderer set to Performance mode");
        }
        
        public static void setMinimal() {
            setAntiAliasing(false);
            setFractionalMetrics(false);
            setEnableShadows(false);
            setEnableOutlines(false);
            setEnableAnimations(false);
            setEnableBatching(false);
            setEnableTextureAtlas(false);
            setBatchSize(10);
            setMaxCacheSize(100);
            System.out.println("Text renderer set to Minimal mode");
        }
    }
    
    /**
     * Accessibility settings
     */
    public static class Accessibility {
        private static boolean highContrast = false;
        private static boolean largeText = false;
        private static float contrastMultiplier = 1.0f;
        private static float sizeMultiplier = 1.0f;
        
        public static boolean isHighContrast() {
            return highContrast;
        }
        
        public static void setHighContrast(boolean highContrast) {
            Accessibility.highContrast = highContrast;
        }
        
        public static boolean isLargeText() {
            return largeText;
        }
        
        public static void setLargeText(boolean largeText) {
            Accessibility.largeText = largeText;
            if (largeText) {
                setSizeMultiplier(1.25f);
            } else {
                setSizeMultiplier(1.0f);
            }
        }
        
        public static float getContrastMultiplier() {
            return contrastMultiplier;
        }
        
        public static void setContrastMultiplier(float contrastMultiplier) {
            Accessibility.contrastMultiplier = Math.max(0.5f, Math.min(3.0f, contrastMultiplier));
        }
        
        public static float getSizeMultiplier() {
            return sizeMultiplier;
        }
        
        public static void setSizeMultiplier(float sizeMultiplier) {
            Accessibility.sizeMultiplier = Math.max(0.75f, Math.min(2.0f, sizeMultiplier));
        }
        
        /**
         * Apply accessibility modifications to a text style
         */
        public static TextStyle applyAccessibility(TextStyle style) {
            TextStyle.Builder builder = TextStyle.builder()
                .font(style.fontFamily)
                .size(style.size)
                .color(style.color)
                .bold(style.bold)
                .italic(style.italic)
                .underline(style.underline)
                .strikethrough(style.strikethrough)
                .shadow(style.shadow)
                .outline(style.outline);
            
            // Apply size multiplier
            if (sizeMultiplier != 1.0f) {
                builder.size(style.size * sizeMultiplier);
            }
            
            // Apply high contrast
            if (highContrast) {
                int colorInt = style.color.toRGB();
                int alpha = (colorInt >> 24) & 0xFF;
                int red = (colorInt >> 16) & 0xFF;
                int green = (colorInt >> 8) & 0xFF;
                int blue = colorInt & 0xFF;
                
                // Increase contrast by pushing colors towards black or white
                float brightness = (red * 0.299f + green * 0.587f + blue * 0.114f) / 255f;
                if (brightness > 0.5f) {
                    // Push towards white
                    red = Math.min(255, (int)(red + (255 - red) * contrastMultiplier));
                    green = Math.min(255, (int)(green + (255 - green) * contrastMultiplier));
                    blue = Math.min(255, (int)(blue + (255 - blue) * contrastMultiplier));
                } else {
                    // Push towards black
                    red = Math.max(0, (int)(red * (1.0f - contrastMultiplier)));
                    green = Math.max(0, (int)(green * (1.0f - contrastMultiplier)));
                    blue = Math.max(0, (int)(blue * (1.0f - contrastMultiplier)));
                }
                
                int newColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
                builder.color(new star.sequoia2.autre.render.AutreRenderer2.Color(newColor));
                
                // Force shadow for better readability
                builder.shadow(true);
            }
            
            return builder.build();
        }
    }
    
    /**
     * Debug and profiling settings
     */
    public static class Debug {
        private static boolean showBounds = false;
        private static boolean showMetrics = false;
        private static boolean logRenderCalls = false;
        private static boolean profilePerformance = false;
        
        public static boolean isShowBounds() {
            return showBounds;
        }
        
        public static void setShowBounds(boolean showBounds) {
            Debug.showBounds = showBounds;
        }
        
        public static boolean isShowMetrics() {
            return showMetrics;
        }
        
        public static void setShowMetrics(boolean showMetrics) {
            Debug.showMetrics = showMetrics;
        }
        
        public static boolean isLogRenderCalls() {
            return logRenderCalls;
        }
        
        public static void setLogRenderCalls(boolean logRenderCalls) {
            Debug.logRenderCalls = logRenderCalls;
        }
        
        public static boolean isProfilePerformance() {
            return profilePerformance;
        }
        
        public static void setProfilePerformance(boolean profilePerformance) {
            Debug.profilePerformance = profilePerformance;
        }
        
        public static void enableAll() {
            setShowBounds(true);
            setShowMetrics(true);
            setLogRenderCalls(true);
            setProfilePerformance(true);
            System.out.println("Text renderer debug mode enabled");
        }
        
        public static void disableAll() {
            setShowBounds(false);
            setShowMetrics(false);
            setLogRenderCalls(false);
            setProfilePerformance(false);
            System.out.println("Text renderer debug mode disabled");
        }
    }
    
    /**
     * Load configuration from properties or save current config
     */
    public static void loadConfig() {
        // TODO: Implement configuration loading from file
        System.out.println("Loading text renderer configuration...");
        PerformancePreset.setBalanced(); // Default to balanced mode
    }
    
    public static void saveConfig() {
        // TODO: Implement configuration saving to file
        System.out.println("Saving text renderer configuration...");
    }
    
    /**
     * Reset all settings to defaults
     */
    public static void resetToDefaults() {
        antiAliasing = true;
        fractionalMetrics = true;
        globalScale = 1.0f;
        maxCacheSize = 1000;
        enableShadows = true;
        enableOutlines = true;
        enableAnimations = true;
        enableRichText = true;
        enableFontCache = true;
        enableTextureAtlas = true;
        enableBatching = true;
        batchSize = 100;
        cacheEvictionThreshold = 0.8f;
        
        Accessibility.highContrast = false;
        Accessibility.largeText = false;
        Accessibility.contrastMultiplier = 1.0f;
        Accessibility.sizeMultiplier = 1.0f;
        
        Debug.disableAll();
        
        System.out.println("Text renderer configuration reset to defaults");
    }
}