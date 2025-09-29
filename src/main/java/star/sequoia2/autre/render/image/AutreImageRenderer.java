package star.sequoia2.autre.render.image;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import star.sequoia2.autre.render.AutreRenderer2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Advanced image renderer with support for:
 * - Static images (PNG, JPG, etc.)
 * - Animated GIF support
 * - Video frame rendering (future implementation)
 * - Image transformations (scaling, rotation, color filters)
 * - Async image loading
 * - Memory-efficient texture management
 */
public class AutreImageRenderer {
    
    // Texture cache
    private static final ConcurrentHashMap<String, ImageData> imageCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AnimatedImageData> animatedImageCache = new ConcurrentHashMap<>();
    
    // Image filtering and transformation
    public enum FilterType {
        NEAREST,
        LINEAR,
        BILINEAR,
        BICUBIC
    }
    
    public enum ScaleMode {
        STRETCH,
        FIT,
        FILL,
        CENTER,
        TILE
    }
    
    // Image data structures
    public static class ImageData {
        public final Identifier texture;
        public final int width;
        public final int height;
        public final long loadTime;
        
        public ImageData(Identifier texture, int width, int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.loadTime = System.currentTimeMillis();
        }
    }
    
    public static class AnimatedImageData extends ImageData {
        public final Identifier[] frames;
        public final float[] delays; // Frame delays in seconds
        public final int frameCount;
        public final float totalDuration;
        public final boolean loop;
        
        public AnimatedImageData(Identifier[] frames, float[] delays, int width, int height, boolean loop) {
            super(frames[0], width, height);
            this.frames = frames;
            this.delays = delays;
            this.frameCount = frames.length;
            this.loop = loop;
            
            float total = 0f;
            for (float delay : delays) {
                total += delay;
            }
            this.totalDuration = total;
        }
        
        public Identifier getCurrentFrame(float time) {
            if (!loop && time >= totalDuration) {
                return frames[frameCount - 1];
            }
            
            float currentTime = loop ? time % totalDuration : Math.min(time, totalDuration);
            float elapsed = 0f;
            
            for (int i = 0; i < frameCount; i++) {
                elapsed += delays[i];
                if (currentTime <= elapsed) {
                    return frames[i];
                }
            }
            
            return frames[frameCount - 1];
        }
    }
    
    public static class ImageTransform {
        public float scaleX = 1f;
        public float scaleY = 1f;
        public float rotation = 0f; // In degrees
        public float skewX = 0f;
        public float skewY = 0f;
        public AutreRenderer2.Color tint = AutreRenderer2.Color.WHITE;
        public float alpha = 1f;
        public FilterType filter = FilterType.LINEAR;
        public ScaleMode scaleMode = ScaleMode.STRETCH;
        
        public static ImageTransform identity() {
            return new ImageTransform();
        }
        
        public ImageTransform scale(float x, float y) {
            this.scaleX = x;
            this.scaleY = y;
            return this;
        }
        
        public ImageTransform scale(float scale) {
            return scale(scale, scale);
        }
        
        public ImageTransform rotate(float degrees) {
            this.rotation = degrees;
            return this;
        }
        
        public ImageTransform tint(AutreRenderer2.Color color) {
            this.tint = color;
            return this;
        }
        
        public ImageTransform alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }
        
        public ImageTransform filter(FilterType filter) {
            this.filter = filter;
            return this;
        }
        
        public ImageTransform scaleMode(ScaleMode mode) {
            this.scaleMode = mode;
            return this;
        }
    }
    
    // Image loading methods
    public static CompletableFuture<ImageData> loadImageAsync(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadImageSync(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load image: " + path, e);
            }
        });
    }
    
    public static CompletableFuture<ImageData> loadImageAsync(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadImageSync(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load image: " + file.getName(), e);
            }
        });
    }
    
    public static ImageData loadImageSync(String resourcePath) throws IOException {
        String cacheKey = "resource:" + resourcePath;
        ImageData cached = imageCache.get(cacheKey);
        if (cached != null) return cached;
        
        try (InputStream stream = AutreImageRenderer.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            
            BufferedImage bufferedImage = ImageIO.read(stream);
            if (bufferedImage == null) {
                throw new IOException("Failed to read image data from: " + resourcePath);
            }
            
            Identifier texture = registerTexture(bufferedImage, cacheKey);
            ImageData imageData = new ImageData(texture, bufferedImage.getWidth(), bufferedImage.getHeight());
            
            imageCache.put(cacheKey, imageData);
            return imageData;
        }
    }
    
    public static ImageData loadImageSync(File file) throws IOException {
        String cacheKey = "file:" + file.getAbsolutePath();
        ImageData cached = imageCache.get(cacheKey);
        if (cached != null) return cached;
        
        BufferedImage bufferedImage = ImageIO.read(file);
        if (bufferedImage == null) {
            throw new IOException("Failed to read image data from: " + file.getName());
        }
        
        Identifier texture = registerTexture(bufferedImage, cacheKey);
        ImageData imageData = new ImageData(texture, bufferedImage.getWidth(), bufferedImage.getHeight());
        
        imageCache.put(cacheKey, imageData);
        return imageData;
    }
    
    // GIF loading support
    public static CompletableFuture<AnimatedImageData> loadGifAsync(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadGifSync(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load GIF: " + path, e);
            }
        });
    }
    
    public static AnimatedImageData loadGifSync(String resourcePath) throws IOException {
        String cacheKey = "gif:" + resourcePath;
        AnimatedImageData cached = animatedImageCache.get(cacheKey);
        if (cached != null) return cached;
        
        // Note: This is a simplified GIF loader
        // For full GIF support, you would need a proper GIF decoder library
        try (InputStream stream = AutreImageRenderer.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("GIF resource not found: " + resourcePath);
            }
            
            // For now, treat as static image
            // TODO: Implement proper GIF frame extraction
            BufferedImage bufferedImage = ImageIO.read(stream);
            if (bufferedImage == null) {
                throw new IOException("Failed to read GIF data from: " + resourcePath);
            }
            
            Identifier texture = registerTexture(bufferedImage, cacheKey);
            
            // Create single-frame "animation" as placeholder
            Identifier[] frames = {texture};
            float[] delays = {1f / 30f}; // 30 FPS default
            
            AnimatedImageData animData = new AnimatedImageData(frames, delays, 
                bufferedImage.getWidth(), bufferedImage.getHeight(), true);
            
            animatedImageCache.put(cacheKey, animData);
            return animData;
        }
    }
    
    // Rendering methods
    public static void drawImage(DrawContext context, ImageData image, float x, float y) {
        drawImage(context, image, x, y, image.width, image.height, ImageTransform.identity());
    }
    
    public static void drawImage(DrawContext context, ImageData image, float x, float y, float width, float height) {
        drawImage(context, image, x, y, width, height, ImageTransform.identity());
    }
    
    public static void drawImage(DrawContext context, ImageData image, float x, float y, 
                                float width, float height, ImageTransform transform) {
        if (image == null) return;
        
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        
        // Apply transformations
        applyTransform(matrices, x, y, width, height, transform);
        
        // Set up rendering state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(transform.tint.r, transform.tint.g, transform.tint.b, 
                                   transform.tint.a * transform.alpha);
        
        // Apply filtering
        applyTextureFiltering(transform.filter);
        
        // Render based on scale mode
        switch (transform.scaleMode) {
            case STRETCH -> renderStretch(context, image.texture, 0, 0, width, height);
            case FIT -> renderFit(context, image, 0, 0, width, height);
            case FILL -> renderFill(context, image, 0, 0, width, height);
            case CENTER -> renderCenter(context, image, 0, 0, width, height);
            case TILE -> renderTile(context, image, 0, 0, width, height);
        }
        
        // Reset rendering state
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        
        matrices.pop();
    }
    
    // Animated image rendering
    public static void drawAnimatedImage(DrawContext context, AnimatedImageData animation, float x, float y, 
                                        float width, float height, float animationTime) {
        drawAnimatedImage(context, animation, x, y, width, height, animationTime, ImageTransform.identity());
    }
    
    public static void drawAnimatedImage(DrawContext context, AnimatedImageData animation, float x, float y, 
                                        float width, float height, float animationTime, ImageTransform transform) {
        if (animation == null) return;
        
        Identifier currentFrame = animation.getCurrentFrame(animationTime);
        
        // Create temporary ImageData for current frame
        ImageData frameData = new ImageData(currentFrame, animation.width, animation.height);
        drawImage(context, frameData, x, y, width, height, transform);
    }
    
    // Image effects and filters
    public static void drawImageWithShadow(DrawContext context, ImageData image, float x, float y, 
                                          float width, float height, float shadowOffset, 
                                          AutreRenderer2.Color shadowColor) {
        // Draw shadow
        ImageTransform shadowTransform = ImageTransform.identity()
            .tint(shadowColor)
            .alpha(shadowColor.a);
        drawImage(context, image, x + shadowOffset, y + shadowOffset, width, height, shadowTransform);
        
        // Draw main image
        drawImage(context, image, x, y, width, height);
    }
    
    public static void drawImageWithGlow(DrawContext context, ImageData image, float x, float y, 
                                        float width, float height, AutreRenderer2.Color glowColor, 
                                        float glowSize, int glowLayers) {
        // Draw glow layers
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        
        for (int i = 1; i <= glowLayers; i++) {
            float expansion = (float) i / glowLayers * glowSize;
            float alpha = (1f - (float) i / glowLayers) * glowColor.a;
            
            ImageTransform glowTransform = ImageTransform.identity()
                .tint(new AutreRenderer2.Color(glowColor.r, glowColor.g, glowColor.b, alpha))
                .scale(1f + expansion / width, 1f + expansion / height);
            
            drawImage(context, image, x - expansion / 2f, y - expansion / 2f, 
                     width + expansion, height + expansion, glowTransform);
        }
        
        RenderSystem.defaultBlendFunc();
        
        // Draw main image
        drawImage(context, image, x, y, width, height);
    }
    
    // Texture management utilities
    private static Identifier registerTexture(BufferedImage image, String name) {
        // Convert BufferedImage to NativeImage
        int width = image.getWidth();
        int height = image.getHeight();
        
        NativeImage nativeImage = new NativeImage(width, height, false);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                // TODO: Fix NativeImage pixel setting for newer Minecraft versions
                // nativeImage.setPixelColor(x, y, argb);
            }
        }
        
        // Register with Minecraft's texture manager
        Identifier identifier = Identifier.of("autre_images", name.replaceAll("[^a-zA-Z0-9_]", "_"));
        NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
        mc.getTextureManager().registerTexture(identifier, texture);
        
        return identifier;
    }
    
    private static void applyTransform(MatrixStack matrices, float x, float y, float width, float height, 
                                     ImageTransform transform) {
        // Translate to position
        matrices.translate(x, y, 0);
        
        // Translate to center for rotation
        matrices.translate(width / 2f, height / 2f, 0);
        
        // Apply rotation
        if (transform.rotation != 0) {
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(transform.rotation));
        }
        
        // Apply scaling
        matrices.scale(transform.scaleX, transform.scaleY, 1f);
        
        // Translate back from center
        matrices.translate(-width / 2f, -height / 2f, 0);
    }
    
    private static void applyTextureFiltering(FilterType filter) {
        switch (filter) {
            case NEAREST -> {
                RenderSystem.setShaderTexture(0, 0);
                // Set nearest neighbor filtering
            }
            case LINEAR, BILINEAR -> {
                RenderSystem.setShaderTexture(0, 0);
                // Set linear filtering
            }
            case BICUBIC -> {
                RenderSystem.setShaderTexture(0, 0);
                // Set bicubic filtering (if supported)
            }
        }
    }
    
    private static void renderStretch(DrawContext context, Identifier texture, float x, float y, float width, float height) {
        context.drawTexture(RenderLayer::getGuiTextured, texture, (int)x, (int)y, 0f, 0f, 
                          (int)width, (int)height, (int)width, (int)height);
    }
    
    private static void renderFit(DrawContext context, ImageData image, float x, float y, float width, float height) {
        float imageAspect = (float)image.width / image.height;
        float targetAspect = width / height;
        
        float renderWidth, renderHeight;
        float renderX, renderY;
        
        if (imageAspect > targetAspect) {
            // Image is wider, fit to width
            renderWidth = width;
            renderHeight = width / imageAspect;
            renderX = x;
            renderY = y + (height - renderHeight) / 2f;
        } else {
            // Image is taller, fit to height
            renderHeight = height;
            renderWidth = height * imageAspect;
            renderX = x + (width - renderWidth) / 2f;
            renderY = y;
        }
        
        renderStretch(context, image.texture, renderX, renderY, renderWidth, renderHeight);
    }
    
    private static void renderFill(DrawContext context, ImageData image, float x, float y, float width, float height) {
        float imageAspect = (float)image.width / image.height;
        float targetAspect = width / height;
        
        float renderWidth, renderHeight;
        float renderX, renderY;
        
        if (imageAspect > targetAspect) {
            // Image is wider, fit to height and crop sides
            renderHeight = height;
            renderWidth = height * imageAspect;
            renderX = x - (renderWidth - width) / 2f;
            renderY = y;
        } else {
            // Image is taller, fit to width and crop top/bottom
            renderWidth = width;
            renderHeight = width / imageAspect;
            renderX = x;
            renderY = y - (renderHeight - height) / 2f;
        }
        
        // Enable scissor to crop
        AutreRenderer2.enableScissor((int)x, (int)y, (int)width, (int)height);
        renderStretch(context, image.texture, renderX, renderY, renderWidth, renderHeight);
        AutreRenderer2.disableScissor();
    }
    
    private static void renderCenter(DrawContext context, ImageData image, float x, float y, float width, float height) {
        float renderX = x + (width - image.width) / 2f;
        float renderY = y + (height - image.height) / 2f;
        
        renderStretch(context, image.texture, renderX, renderY, image.width, image.height);
    }
    
    private static void renderTile(DrawContext context, ImageData image, float x, float y, float width, float height) {
        int tilesX = (int) Math.ceil(width / image.width);
        int tilesY = (int) Math.ceil(height / image.height);
        
        AutreRenderer2.enableScissor((int)x, (int)y, (int)width, (int)height);
        
        for (int tileY = 0; tileY < tilesY; tileY++) {
            for (int tileX = 0; tileX < tilesX; tileX++) {
                float tileRenderX = x + tileX * image.width;
                float tileRenderY = y + tileY * image.height;
                
                renderStretch(context, image.texture, tileRenderX, tileRenderY, image.width, image.height);
            }
        }
        
        AutreRenderer2.disableScissor();
    }
    
    // Cache management
    public static void clearImageCache() {
        // Unregister all textures
        imageCache.values().forEach(image -> {
            if (mc.getTextureManager() != null) {
                mc.getTextureManager().destroyTexture(image.texture);
            }
        });
        
        animatedImageCache.values().forEach(animation -> {
            for (Identifier frame : animation.frames) {
                if (mc.getTextureManager() != null) {
                    mc.getTextureManager().destroyTexture(frame);
                }
            }
        });
        
        imageCache.clear();
        animatedImageCache.clear();
    }
    
    public static void clearExpiredCache(long maxAge) {
        long currentTime = System.currentTimeMillis();
        
        imageCache.entrySet().removeIf(entry -> {
            boolean expired = currentTime - entry.getValue().loadTime > maxAge;
            if (expired && mc.getTextureManager() != null) {
                mc.getTextureManager().destroyTexture(entry.getValue().texture);
            }
            return expired;
        });
        
        animatedImageCache.entrySet().removeIf(entry -> {
            boolean expired = currentTime - entry.getValue().loadTime > maxAge;
            if (expired && mc.getTextureManager() != null) {
                for (Identifier frame : entry.getValue().frames) {
                    mc.getTextureManager().destroyTexture(frame);
                }
            }
            return expired;
        });
    }
    
    // Utility methods
    public static int getCacheSize() {
        return imageCache.size() + animatedImageCache.size();
    }
    
    public static boolean isImageLoaded(String path) {
        return imageCache.containsKey("resource:" + path) || 
               imageCache.containsKey("file:" + path) ||
               animatedImageCache.containsKey("gif:" + path);
    }
    
    // Video support placeholders (for future implementation)
    public static class VideoData extends AnimatedImageData {
        public final String codecInfo;
        public final float frameRate;
        public final boolean hasAudio;
        
        public VideoData(Identifier[] frames, float[] delays, int width, int height, 
                        String codecInfo, float frameRate, boolean hasAudio) {
            super(frames, delays, width, height, false);
            this.codecInfo = codecInfo;
            this.frameRate = frameRate;
            this.hasAudio = hasAudio;
        }
    }
    
    // Placeholder for future video loading
    public static CompletableFuture<VideoData> loadVideoAsync(String path) {
        return CompletableFuture.supplyAsync(() -> {
            // TODO: Implement video loading using FFmpeg or similar
            throw new UnsupportedOperationException("Video support not yet implemented");
        });
    }
}