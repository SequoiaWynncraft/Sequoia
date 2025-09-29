package star.sequoia2.autre.render.text;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import star.sequoia2.autre.render.text.AutreTextRenderer.TextStyle;

/**
 * Font manager for AutreTextRenderer
 * Handles font loading, caching, and management
 */
public class AutreFontManager {
    
    private static final Map<String, FontInfo> registeredFonts = new LinkedHashMap<>();
    
    public static class FontInfo {
        public final String name;
        public final String displayName;
        public final String path;
        public final boolean isResource;
        public final Set<Float> preloadedSizes;
        public final boolean isLoaded;
        
        public FontInfo(String name, String displayName, String path, boolean isResource, Set<Float> preloadedSizes, boolean isLoaded) {
            this.name = name;
            this.displayName = displayName;
            this.path = path;
            this.isResource = isResource;
            this.preloadedSizes = new HashSet<>(preloadedSizes);
            this.isLoaded = isLoaded;
        }
    }
    
    // Default fonts that should be preloaded
    private static final String[] DEFAULT_FONT_SIZES = {"8", "9", "10", "12", "14", "16", "18", "24", "32"};
    
    /**
     * Initialize the font manager and load default fonts
     */
    public static void initialize() {
        // Register minecraft font
        registeredFonts.put("minecraft", new FontInfo("minecraft", "Minecraft Default", "", false, 
            Set.of(9f), true));
        
        // Load common system fonts if available
        loadSystemFonts();
        
        System.out.println("AutreFontManager initialized with " + registeredFonts.size() + " fonts");
    }
    
    /**
     * Register and load a custom font from file
     */
    public static CompletableFuture<Boolean> loadCustomFontAsync(String fontName, File fontFile) {
        return loadCustomFontAsync(fontName, fontName, fontFile);
    }
    
    public static CompletableFuture<Boolean> loadCustomFontAsync(String fontName, String displayName, File fontFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean success = AutreTextRenderer.loadCustomFont(fontName, fontFile);
                if (success) {
                    Set<Float> sizes = new HashSet<>();
                    for (String size : DEFAULT_FONT_SIZES) {
                        sizes.add(Float.parseFloat(size));
                    }
                    
                    registeredFonts.put(fontName, new FontInfo(fontName, displayName, 
                        fontFile.getAbsolutePath(), false, sizes, true));
                    
                    System.out.println("Loaded custom font: " + displayName + " (" + fontName + ")");
                }
                return success;
            } catch (Exception e) {
                System.err.println("Failed to load font " + fontName + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Register and load a custom font from resource
     */
    public static CompletableFuture<Boolean> loadCustomFontFromResourceAsync(String fontName, String resourcePath) {
        return loadCustomFontFromResourceAsync(fontName, fontName, resourcePath);
    }
    
    public static CompletableFuture<Boolean> loadCustomFontFromResourceAsync(String fontName, String displayName, String resourcePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean success = AutreTextRenderer.loadCustomFont(fontName, resourcePath);
                if (success) {
                    Set<Float> sizes = new HashSet<>();
                    for (String size : DEFAULT_FONT_SIZES) {
                        sizes.add(Float.parseFloat(size));
                    }
                    
                    registeredFonts.put(fontName, new FontInfo(fontName, displayName, 
                        resourcePath, true, sizes, true));
                    
                    System.out.println("Loaded custom font from resource: " + displayName + " (" + fontName + ")");
                }
                return success;
            } catch (Exception e) {
                System.err.println("Failed to load font from resource " + fontName + ": " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Load multiple fonts asynchronously
     */
    public static CompletableFuture<Map<String, Boolean>> loadFontsAsync(Map<String, String> fontPaths) {
        List<CompletableFuture<Map.Entry<String, Boolean>>> futures = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : fontPaths.entrySet()) {
            String fontName = entry.getKey();
            String path = entry.getValue();
            
            CompletableFuture<Map.Entry<String, Boolean>> future = CompletableFuture.supplyAsync(() -> {
                boolean success;
                if (path.startsWith("/") || path.startsWith("assets/")) {
                    success = AutreTextRenderer.loadCustomFont(fontName, path);
                } else {
                    success = AutreTextRenderer.loadCustomFont(fontName, new File(path));
                }
                return Map.entry(fontName, success);
            });
            
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                Map<String, Boolean> results = new HashMap<>();
                for (CompletableFuture<Map.Entry<String, Boolean>> future : futures) {
                    try {
                        Map.Entry<String, Boolean> result = future.get();
                        results.put(result.getKey(), result.getValue());
                        
                        if (result.getValue()) {
                            String fontName = result.getKey();
                            registeredFonts.put(fontName, new FontInfo(fontName, fontName, 
                                fontPaths.get(fontName), false, Set.of(), true));
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading font: " + e.getMessage());
                    }
                }
                return results;
            });
    }
    
    /**
     * Get all available fonts
     */
    public static List<FontInfo> getAvailableFonts() {
        return new ArrayList<>(registeredFonts.values());
    }
    
    /**
     * Get font info by name
     */
    public static FontInfo getFontInfo(String fontName) {
        return registeredFonts.get(fontName);
    }
    
    /**
     * Check if a font is available
     */
    public static boolean isFontAvailable(String fontName) {
        FontInfo info = registeredFonts.get(fontName);
        return info != null && info.isLoaded;
    }
    
    /**
     * Get font names for UI display
     */
    public static List<String> getFontNames() {
        return new ArrayList<>(registeredFonts.keySet());
    }
    
    /**
     * Get font display names for UI
     */
    public static List<String> getFontDisplayNames() {
        return registeredFonts.values().stream()
            .map(info -> info.displayName)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Create a text style with specified font
     */
    public static TextStyle.Builder createStyleWithFont(String fontName) {
        if (!isFontAvailable(fontName)) {
            fontName = "minecraft"; // Fallback to minecraft font
        }
        
        return TextStyle.builder().font(fontName);
    }
    
    /**
     * Create commonly used text styles
     */
    public static class Styles {
        
        public static TextStyle heading1(String fontName) {
            return createStyleWithFont(fontName)
                .size(24f)
                .bold(true)
                .build();
        }
        
        public static TextStyle heading2(String fontName) {
            return createStyleWithFont(fontName)
                .size(20f)
                .bold(true)
                .build();
        }
        
        public static TextStyle heading3(String fontName) {
            return createStyleWithFont(fontName)
                .size(16f)
                .bold(true)
                .build();
        }
        
        public static TextStyle body(String fontName) {
            return createStyleWithFont(fontName)
                .size(12f)
                .build();
        }
        
        public static TextStyle caption(String fontName) {
            return createStyleWithFont(fontName)
                .size(10f)
                .build();
        }
        
        public static TextStyle button(String fontName) {
            return createStyleWithFont(fontName)
                .size(12f)
                .bold(true)
                .build();
        }
        
        public static TextStyle link(String fontName) {
            return createStyleWithFont(fontName)
                .size(12f)
                .underline(true)
                .color(star.sequoia2.autre.render.AutreRenderer2.Color.PRIMARY)
                .build();
        }
    }
    
    /**
     * Load common system fonts
     */
    private static void loadSystemFonts() {
        String os = System.getProperty("os.name").toLowerCase();
        
        // Common system font paths
        Map<String, String> systemFonts = new HashMap<>();
        
        if (os.contains("win")) {
            // Windows fonts
            systemFonts.put("arial", "C:/Windows/Fonts/arial.ttf");
            systemFonts.put("calibri", "C:/Windows/Fonts/calibri.ttf");
            systemFonts.put("segoeui", "C:/Windows/Fonts/segoeui.ttf");
            systemFonts.put("tahoma", "C:/Windows/Fonts/tahoma.ttf");
            systemFonts.put("verdana", "C:/Windows/Fonts/verdana.ttf");
        } else if (os.contains("mac")) {
            // macOS fonts
            systemFonts.put("helvetica", "/System/Library/Fonts/Helvetica.ttc");
            systemFonts.put("times", "/System/Library/Fonts/Times.ttc");
            systemFonts.put("courier", "/System/Library/Fonts/Courier.ttc");
            systemFonts.put("geneva", "/System/Library/Fonts/Geneva.ttf");
        } else if (os.contains("nix") || os.contains("nux")) {
            // Linux fonts (common paths)
            systemFonts.put("dejavu", "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf");
            systemFonts.put("liberation", "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf");
            systemFonts.put("ubuntu", "/usr/share/fonts/truetype/ubuntu/Ubuntu-Regular.ttf");
            systemFonts.put("opensans", "/usr/share/fonts/truetype/opensans/OpenSans-Regular.ttf");
        }
        
        // Try to load system fonts
        for (Map.Entry<String, String> entry : systemFonts.entrySet()) {
            String fontName = entry.getKey();
            String path = entry.getValue();
            File fontFile = new File(path);
            
            if (fontFile.exists()) {
                try {
                    boolean success = AutreTextRenderer.loadCustomFont(fontName, fontFile);
                    if (success) {
                        registeredFonts.put(fontName, new FontInfo(fontName, 
                            capitalizeWords(fontName), path, false, Set.of(), true));
                        System.out.println("Loaded system font: " + fontName);
                    }
                } catch (Exception e) {
                    // Silently ignore system font loading errors
                }
            }
        }
    }
    
    /**
     * Utility method to capitalize words
     */
    private static String capitalizeWords(String str) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c) || c == '_' || c == '-') {
                result.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Font loading presets for common use cases
     */
    public static class Presets {
        
        /**
         * Load Google Fonts pack (requires font files to be present)
         */
        public static CompletableFuture<Map<String, Boolean>> loadGoogleFonts() {
            Map<String, String> googleFonts = Map.of(
                "roboto", "/assets/autre_fonts/Roboto-Regular.ttf",
                "opensans", "/assets/autre_fonts/OpenSans-Regular.ttf",
                "lato", "/assets/autre_fonts/Lato-Regular.ttf",
                "montserrat", "/assets/autre_fonts/Montserrat-Regular.ttf",
                "nunito", "/assets/autre_fonts/Nunito-Regular.ttf",
                "poppins", "/assets/autre_fonts/Poppins-Regular.ttf"
            );
            
            return loadFontsAsync(googleFonts);
        }
        
        /**
         * Load programming fonts pack
         */
        public static CompletableFuture<Map<String, Boolean>> loadProgrammingFonts() {
            Map<String, String> codeFonts = Map.of(
                "jetbrains", "/assets/autre_fonts/JetBrainsMono-Regular.ttf",
                "firacode", "/assets/autre_fonts/FiraCode-Regular.ttf",
                "sourcecodepro", "/assets/autre_fonts/SourceCodePro-Regular.ttf",
                "cascadia", "/assets/autre_fonts/CascadiaCode.ttf",
                "inconsolata", "/assets/autre_fonts/Inconsolata-Regular.ttf"
            );
            
            return loadFontsAsync(codeFonts);
        }
        
        /**
         * Load UI fonts pack
         */
        public static CompletableFuture<Map<String, Boolean>> loadUIFonts() {
            Map<String, String> uiFonts = Map.of(
                "inter", "/assets/autre_fonts/Inter-Regular.ttf",
                "system", "/assets/autre_fonts/SystemUI.ttf",
                "segoeui", "/assets/autre_fonts/SegoeUI.ttf",
                "helvetica", "/assets/autre_fonts/Helvetica.ttf"
            );
            
            return loadFontsAsync(uiFonts);
        }
    }
}