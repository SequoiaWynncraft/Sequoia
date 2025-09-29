package star.sequoia2.autre.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import star.sequoia2.autre.render.text.AutreTextRenderer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static star.sequoia2.client.SeqClient.mc;

/**
 * AutreRenderer22 - Advanced GPU-accelerated renderer with transparency, blur, animations, and partial redraw
 * Designed for high-performance modern UI rendering with minimal GPU overhead
 */
public class AutreRenderer2 {

    // Core color system with advanced features
    public static class Color {
        public final float r, g, b, a;
        private final int rgbaCache;
        
        public Color(float r, float g, float b, float a) {
            this.r = Math.max(0f, Math.min(1f, r));
            this.g = Math.max(0f, Math.min(1f, g));
            this.b = Math.max(0f, Math.min(1f, b));
            this.a = Math.max(0f, Math.min(1f, a));
            this.rgbaCache = ((int)(this.a * 255) << 24) | ((int)(this.r * 255) << 16) | 
                            ((int)(this.g * 255) << 8) | (int)(this.b * 255);
        }
        
        public Color(int rgba) {
            this.a = ((rgba >> 24) & 0xFF) / 255f;
            this.r = ((rgba >> 16) & 0xFF) / 255f;
            this.g = ((rgba >> 8) & 0xFF) / 255f;
            this.b = (rgba & 0xFF) / 255f;
            this.rgbaCache = rgba;
        }
        
        public Color withAlpha(float alpha) { return new Color(r, g, b, alpha); }
        public Color withRed(float red) { return new Color(red, g, b, a); }
        public Color withGreen(float green) { return new Color(r, green, b, a); }
        public Color withBlue(float blue) { return new Color(r, g, blue, a); }
        
        public Color lerp(Color other, float t) {
            t = Math.max(0f, Math.min(1f, t));
            return new Color(
                r + (other.r - r) * t,
                g + (other.g - g) * t,
                b + (other.b - b) * t,
                a + (other.a - a) * t
            );
        }
        
        public Color multiply(float factor) {
            return new Color(r * factor, g * factor, b * factor, a);
        }
        
        public Color lighter(float factor) {
            return new Color(
                Math.min(1f, r + factor),
                Math.min(1f, g + factor),
                Math.min(1f, b + factor),
                a
            );
        }
        
        public Color darker(float factor) {
            return new Color(
                Math.max(0f, r - factor),
                Math.max(0f, g - factor),
                Math.max(0f, b - factor),
                a
            );
        }
        
        public Color blend(Color other, BlendMode mode) {
            switch (mode) {
                case MULTIPLY: return new Color(r * other.r, g * other.g, b * other.b, a * other.a);
                case ADD: return new Color(Math.min(1f, r + other.r), Math.min(1f, g + other.g), 
                                         Math.min(1f, b + other.b), Math.min(1f, a + other.a));
                case OVERLAY: return overlay(other);
                default: return this;
            }
        }
        
        private Color overlay(Color other) {
            float nr = r < 0.5f ? 2 * r * other.r : 1 - 2 * (1 - r) * (1 - other.r);
            float ng = g < 0.5f ? 2 * g * other.g : 1 - 2 * (1 - g) * (1 - other.g);
            float nb = b < 0.5f ? 2 * b * other.b : 1 - 2 * (1 - b) * (1 - other.b);
            return new Color(nr, ng, nb, a);
        }
        
        public int getRGBA() { return rgbaCache; }
        public int getRGB() { return rgbaCache & 0x00FFFFFF; }
        
        public int toRGB() { return getRGB(); } // Alias for getRGB()
        
        // Predefined colors
        public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
        public static final Color WHITE = new Color(1, 1, 1, 1);
        public static final Color BLACK = new Color(0, 0, 0, 1);
        public static final Color RED = new Color(1, 0, 0, 1);
        public static final Color GREEN = new Color(0, 1, 0, 1);
        public static final Color BLUE = new Color(0, 0, 1, 1);
        
        // UI Theme colors
        public static final Color BACKGROUND_PRIMARY = new Color(0.08f, 0.08f, 0.08f, 0.95f);
        public static final Color BACKGROUND_SECONDARY = new Color(0.12f, 0.12f, 0.12f, 1f);
        public static final Color BACKGROUND = BACKGROUND_PRIMARY; // Alias for backward compatibility
        public static final Color SURFACE = new Color(0.16f, 0.16f, 0.16f, 1f);
        public static final Color ACCENT = new Color(0.2f, 0.6f, 1f, 1f);
        public static final Color TEXT_PRIMARY = new Color(0.9f, 0.9f, 0.9f, 1f);
        public static final Color TEXT_SECONDARY = new Color(0.7f, 0.7f, 0.7f, 1f);
        public static final Color PRIMARY = TEXT_PRIMARY; // Alias for TEXT_PRIMARY
        public static final Color SECONDARY = TEXT_SECONDARY; // Alias for TEXT_SECONDARY
        
        // Predefined accent colors
        public static final Color ACCENT_CYAN = new Color(0.0f, 0.8f, 0.8f, 1f);
        public static final Color ACCENT_BLUE = new Color(0.2f, 0.6f, 1f, 1f);
        public static final Color ACCENT_GREEN = new Color(0.2f, 0.8f, 0.4f, 1f);
        public static final Color ACCENT_ORANGE = new Color(1f, 0.6f, 0.2f, 1f);
        public static final Color ACCENT_PURPLE = new Color(0.8f, 0.4f, 1f, 1f);
        
        // Static accent color system
        private static Color currentAccent = ACCENT_BLUE; // Default accent color
        
        /**
         * Get the current accent color
         */
        public static Color getAccent() {
            return currentAccent;
        }
        
        /**
         * Set the current accent color
         */
        public static void setAccent(Color accent) {
            currentAccent = accent;
        }
    }

    // Blend modes for advanced color mixing
    public enum BlendMode {
        NORMAL, MULTIPLY, ADD, OVERLAY, SCREEN, SOFT_LIGHT
    }

    // Animation system
    public static class Animation {
        private final long duration;
        private final EasingFunction easing;
        private long startTime;
        private boolean active;
        
        public Animation(long durationMs, EasingFunction easing) {
            this.duration = durationMs;
            this.easing = easing;
            this.active = false;
        }
        
        public void start() {
            startTime = System.currentTimeMillis();
            active = true;
        }
        
        public void stop() {
            active = false;
        }
        
        public boolean isActive() {
            return active && getProgress() < 1f;
        }
        
        public float getProgress() {
            if (!active) return 0f;
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= duration) {
                active = false;
                return 1f;
            }
            return easing.apply(elapsed / (float) duration);
        }
        
        public float getValue(float start, float end) {
            return start + (end - start) * getProgress();
        }
        
        public Color getValue(Color start, Color end) {
            return start.lerp(end, getProgress());
        }
    }

    // Easing functions for smooth animations
    public interface EasingFunction {
        float apply(float t);
        
        EasingFunction LINEAR = t -> t;
        EasingFunction EASE_IN_QUAD = t -> t * t;
        EasingFunction EASE_OUT_QUAD = t -> 1 - (1 - t) * (1 - t);
        EasingFunction EASE_IN_OUT_QUAD = t -> t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
        EasingFunction EASE_IN_CUBIC = t -> t * t * t;
        EasingFunction EASE_OUT_CUBIC = t -> 1 - (float) Math.pow(1 - t, 3);
        EasingFunction EASE_IN_OUT_CUBIC = t -> t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
        EasingFunction BOUNCE = t -> {
            if (t < 1 / 2.75f) return 7.5625f * t * t;
            if (t < 2 / 2.75f) return 7.5625f * (t -= 1.5f / 2.75f) * t + 0.75f;
            if (t < 2.5 / 2.75f) return 7.5625f * (t -= 2.25f / 2.75f) * t + 0.9375f;
            return 7.5625f * (t -= 2.625f / 2.75f) * t + 0.984375f;
        };
    }

    // Render layer system for efficient partial redraw
    public static class RenderLayer {
        private final String id;
        private final int zIndex;
        private boolean dirty;
        private final List<RenderCommand> commands;
        private final Rectangle bounds;
        
        public RenderLayer(String id, int zIndex) {
            this.id = id;
            this.zIndex = zIndex;
            this.dirty = true;
            this.commands = new CopyOnWriteArrayList<>();
            this.bounds = new Rectangle();
        }
        
        public void addCommand(RenderCommand command) {
            commands.add(command);
            expandBounds(command.getBounds());
            markDirty();
        }
        
        public void clearCommands() {
            commands.clear();
            bounds.clear();
            markDirty();
        }
        
        public void markDirty() {
            dirty = true;
        }
        
        public boolean isDirty() {
            return dirty;
        }
        
        public void markClean() {
            dirty = false;
        }
        
        private void expandBounds(Rectangle commandBounds) {
            if (bounds.isEmpty()) {
                bounds.set(commandBounds);
            } else {
                bounds.expand(commandBounds);
            }
        }
        
        public List<RenderCommand> getCommands() { return commands; }
        public Rectangle getBounds() { return bounds; }
        public String getId() { return id; }
        public int getZIndex() { return zIndex; }
    }

    // Render command interface for batching
    public interface RenderCommand {
        void execute(DrawContext context);
        Rectangle getBounds();
        boolean canBatch(RenderCommand other);
    }

    // Rectangle utility class
    public static class Rectangle {
        public float x, y, width, height;
        
        public Rectangle() { this(0, 0, 0, 0); }
        public Rectangle(float x, float y, float width, float height) {
            set(x, y, width, height);
        }
        
        public void set(float x, float y, float width, float height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }
        
        public void set(Rectangle other) {
            set(other.x, other.y, other.width, other.height);
        }
        
        public void expand(Rectangle other) {
            if (other.isEmpty()) return;
            if (isEmpty()) { set(other); return; }
            
            float minX = Math.min(x, other.x);
            float minY = Math.min(y, other.y);
            float maxX = Math.max(x + width, other.x + other.width);
            float maxY = Math.max(y + height, other.y + other.height);
            set(minX, minY, maxX - minX, maxY - minY);
        }
        
        public boolean intersects(Rectangle other) {
            return x < other.x + other.width && x + width > other.x &&
                   y < other.y + other.height && y + height > other.y;
        }
        
        public boolean contains(float px, float py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
        
        public boolean isEmpty() { return width <= 0 || height <= 0; }
        public void clear() { set(0, 0, 0, 0); }
    }

    // Blur effect system
    public static class BlurEffect {
        private final float intensity;
        private final int passes;
        private final BlurType type;
        
        public BlurEffect(float intensity, BlurType type) {
            this.intensity = Math.max(0f, intensity);
            this.passes = Math.max(1, (int) Math.ceil(intensity / 2f));
            this.type = type;
        }
        
        public float getIntensity() { return intensity; }
        public int getPasses() { return passes; }
        public BlurType getType() { return type; }
    }
    
    public enum BlurType {
        GAUSSIAN, BOX, MOTION
    }

    // Core renderer state
    private static final Map<String, RenderLayer> layers = new ConcurrentHashMap<>();
    private static final List<String> layerOrder = new ArrayList<>();
    private static final Rectangle clipBounds = new Rectangle();
    private static boolean clipping = false;
    private static final Map<String, Animation> activeAnimations = new ConcurrentHashMap<>();

    // Render state management
    private static MatrixStack currentMatrices;
    private static boolean renderingActive = false;

    /**
     * Initialize renderer and create default layers
     */
    public static void initialize() {
        // Create default layers in order
        createLayer("background", 0);
        createLayer("content", 100);
        createLayer("ui", 200);
        createLayer("overlay", 300);
        createLayer("tooltip", 400);
        
        System.out.println("AutreRenderer22 initialized with " + layers.size() + " layers");
    }

    /**
     * Create a new render layer
     */
    public static RenderLayer createLayer(String id, int zIndex) {
        RenderLayer layer = new RenderLayer(id, zIndex);
        layers.put(id, layer);
        
        // Insert in correct z-order
        layerOrder.removeIf(layerId -> layerId.equals(id));
        int insertIndex = 0;
        for (String layerId : layerOrder) {
            if (layers.get(layerId).getZIndex() > zIndex) break;
            insertIndex++;
        }
        layerOrder.add(insertIndex, id);
        
        return layer;
    }

    /**
     * Get or create layer
     */
    public static RenderLayer getLayer(String id) {
        return layers.computeIfAbsent(id, key -> createLayer(key, 100));
    }

    /**
     * Begin frame rendering
     */
    public static void beginFrame(DrawContext context) {
        currentMatrices = context.getMatrices();
        renderingActive = true;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    /**
     * End frame rendering and execute all dirty layers
     */
    public static void endFrame() {
        if (!renderingActive) return;
        
        // Render layers in z-order, only if dirty or intersecting with dirty regions
        for (String layerId : layerOrder) {
            RenderLayer layer = layers.get(layerId);
            if (layer != null && layer.isDirty()) {
                renderLayer(layer);
                layer.markClean();
            }
        }
        
        // Update animations
        activeAnimations.entrySet().removeIf(entry -> !entry.getValue().isActive());
        
        RenderSystem.disableBlend();
        renderingActive = false;
        currentMatrices = null;
    }

    /**
     * Render a specific layer
     */
    private static void renderLayer(RenderLayer layer) {
        if (layer.getCommands().isEmpty()) return;
        
        // Batch similar commands for efficiency
        List<List<RenderCommand>> batches = batchCommands(layer.getCommands());
        
        for (List<RenderCommand> batch : batches) {
            if (batch.size() == 1) {
                batch.get(0).execute(new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers()));
            } else {
                // Execute batched commands
                executeBatch(batch);
            }
        }
    }

    /**
     * Batch similar render commands for GPU efficiency
     */
    private static List<List<RenderCommand>> batchCommands(List<RenderCommand> commands) {
        List<List<RenderCommand>> batches = new ArrayList<>();
        List<RenderCommand> currentBatch = null;
        
        for (RenderCommand command : commands) {
            if (currentBatch == null || currentBatch.isEmpty() || 
                !currentBatch.get(0).canBatch(command)) {
                currentBatch = new ArrayList<>();
                batches.add(currentBatch);
            }
            currentBatch.add(command);
        }
        
        return batches;
    }

    /**
     * Execute a batch of similar commands efficiently
     */
    private static void executeBatch(List<RenderCommand> batch) {
        // For now, execute individually - could be optimized with instanced rendering
        DrawContext context = new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());
        for (RenderCommand command : batch) {
            command.execute(context);
        }
    }

    /**
     * Advanced rectangle rendering with effects
     */
    public static void fillRect(String layerId, float x, float y, float width, float height, Color color) {
        fillRect(layerId, x, y, width, height, color, null, null);
    }

    public static void fillRect(String layerId, float x, float y, float width, float height, 
                               Color color, BlurEffect blur, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            // Apply animation (assuming it's a color animation for simplicity)
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        RectCommand command = new RectCommand(x, y, width, height, renderColor, blur, false);
        layer.addCommand(command);
    }
    
    /**
     * Direct rectangle rendering - for immediate rendering with MatrixStack
     */
    public static void fillRect(MatrixStack matrices, float x, float y, float width, float height, Color color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        buffer.vertex(matrix, x, y, 0f).color(color.r, color.g, color.b, color.a);
        buffer.vertex(matrix, x, y + height, 0f).color(color.r, color.g, color.b, color.a);
        buffer.vertex(matrix, x + width, y + height, 0f).color(color.r, color.g, color.b, color.a);
        buffer.vertex(matrix, x + width, y, 0f).color(color.r, color.g, color.b, color.a);
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
    
    /**
     * Direct rounded rectangle rendering - for immediate rendering with MatrixStack
     */
    public static void fillRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        // For now, fall back to regular rectangle
        // TODO: Implement proper rounded rectangle rendering
        fillRect(matrices, x, y, width, height, color);
    }
    
    /**
     * Direct stroke rectangle rendering - for immediate rendering with MatrixStack
     */
    public static void strokeRect(MatrixStack matrices, float x, float y, float width, float height, float strokeWidth, Color color) {
        // Draw four rectangles to form a stroke
        // Top
        fillRect(matrices, x, y, width, strokeWidth, color);
        // Bottom
        fillRect(matrices, x, y + height - strokeWidth, width, strokeWidth, color);
        // Left
        fillRect(matrices, x, y, strokeWidth, height, color);
        // Right
        fillRect(matrices, x + width - strokeWidth, y, strokeWidth, height, color);
    }

    /**
     * Direct filled circle rendering - for immediate rendering with MatrixStack
     */
    public static void fillCircle(MatrixStack matrices, float centerX, float centerY, float radius, Color color) {
        fillCircle(matrices, centerX, centerY, radius, color, 32);
    }

    public static void fillCircle(MatrixStack matrices, float centerX, float centerY, float radius, Color color, int segments) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        // Center vertex
        buffer.vertex(matrix, centerX, centerY, 0f).color(color.r, color.g, color.b, color.a);

        // Circle vertices
        for (int i = 0; i <= segments; i++) {
            float angle = (float)(i * 2.0 * Math.PI / segments);
            float x = centerX + radius * (float)Math.cos(angle);
            float y = centerY + radius * (float)Math.sin(angle);
            buffer.vertex(matrix, x, y, 0f).color(color.r, color.g, color.b, color.a);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    /**
     * Direct circle stroke rendering - for immediate rendering with MatrixStack
     */
    public static void strokeCircle(MatrixStack matrices, float centerX, float centerY, float radius, float strokeWidth, Color color) {
        strokeCircle(matrices, centerX, centerY, radius, strokeWidth, color, 32);
    }

    public static void strokeCircle(MatrixStack matrices, float centerX, float centerY, float radius, float strokeWidth, Color color, int segments) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        float innerRadius = radius - strokeWidth / 2f;
        float outerRadius = radius + strokeWidth / 2f;

        for (int i = 0; i <= segments; i++) {
            float angle = (float)(i * 2.0 * Math.PI / segments);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);

            // Inner vertex
            float innerX = centerX + innerRadius * cos;
            float innerY = centerY + innerRadius * sin;
            buffer.vertex(matrix, innerX, innerY, 0f).color(color.r, color.g, color.b, color.a);

            // Outer vertex
            float outerX = centerX + outerRadius * cos;
            float outerY = centerY + outerRadius * sin;
            buffer.vertex(matrix, outerX, outerY, 0f).color(color.r, color.g, color.b, color.a);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    /**
     * Direct filled arc rendering - for immediate rendering with MatrixStack
     * @param startAngle Starting angle in radians
     * @param endAngle Ending angle in radians
     */
    public static void fillArc(MatrixStack matrices, float centerX, float centerY, float radius, 
                              float startAngle, float endAngle, Color color) {
        fillArc(matrices, centerX, centerY, radius, startAngle, endAngle, color, 32);
    }

    public static void fillArc(MatrixStack matrices, float centerX, float centerY, float radius, 
                              float startAngle, float endAngle, Color color, int segments) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        
        // Center vertex
        buffer.vertex(matrix, centerX, centerY, 0f).color(color.r, color.g, color.b, color.a);
        
        // Normalize angles
        while (endAngle < startAngle) endAngle += 2 * Math.PI;
        float angleRange = endAngle - startAngle;
        
        // Arc vertices
        int arcSegments = Math.max(1, (int)(segments * angleRange / (2 * Math.PI)));
        for (int i = 0; i <= arcSegments; i++) {
            float angle = startAngle + (angleRange * i / arcSegments);
            float x = centerX + radius * (float)Math.cos(angle);
            float y = centerY + radius * (float)Math.sin(angle);
            buffer.vertex(matrix, x, y, 0f).color(color.r, color.g, color.b, color.a);
        }
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    /**
     * Direct arc stroke rendering - for immediate rendering with MatrixStack
     * @param startAngle Starting angle in radians
     * @param endAngle Ending angle in radians
     */
    public static void strokeArc(MatrixStack matrices, float centerX, float centerY, float radius, 
                                float startAngle, float endAngle, float strokeWidth, Color color) {
        strokeArc(matrices, centerX, centerY, radius, startAngle, endAngle, strokeWidth, color, 32);
    }

    public static void strokeArc(MatrixStack matrices, float centerX, float centerY, float radius, 
                                float startAngle, float endAngle, float strokeWidth, Color color, int segments) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        
        float innerRadius = radius - strokeWidth / 2f;
        float outerRadius = radius + strokeWidth / 2f;
        
        // Normalize angles
        while (endAngle < startAngle) endAngle += 2 * Math.PI;
        float angleRange = endAngle - startAngle;
        
        // Arc vertices
        int arcSegments = Math.max(1, (int)(segments * angleRange / (2 * Math.PI)));
        for (int i = 0; i <= arcSegments; i++) {
            float angle = startAngle + (angleRange * i / arcSegments);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            
            // Inner vertex
            float innerX = centerX + innerRadius * cos;
            float innerY = centerY + innerRadius * sin;
            buffer.vertex(matrix, innerX, innerY, 0f).color(color.r, color.g, color.b, color.a);
            
            // Outer vertex
            float outerX = centerX + outerRadius * cos;
            float outerY = centerY + outerRadius * sin;
            buffer.vertex(matrix, outerX, outerY, 0f).color(color.r, color.g, color.b, color.a);
        }
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    /**
     * Rounded rectangle with advanced effects
     */
    public static void fillRoundedRect(String layerId, float x, float y, float width, float height, 
                                     float radius, Color color) {
        fillRoundedRect(layerId, x, y, width, height, radius, color, null, null);
    }

    public static void fillRoundedRect(String layerId, float x, float y, float width, float height, 
                                     float radius, Color color, BlurEffect blur, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        RoundedRectCommand command = new RoundedRectCommand(x, y, width, height, radius, renderColor, blur);
        layer.addCommand(command);
    }

    /**
     * Gradient rectangle with transparency support
     */
    public static void fillGradientRect(String layerId, float x, float y, float width, float height,
                                      Color startColor, Color endColor, boolean vertical) {
        RenderLayer layer = getLayer(layerId);
        GradientRectCommand command = new GradientRectCommand(x, y, width, height, startColor, endColor, vertical);
        layer.addCommand(command);
    }

    /**
     * Layered circle rendering with effects
     */
    public static void fillCircle(String layerId, float centerX, float centerY, float radius, Color color) {
        fillCircle(layerId, centerX, centerY, radius, color, null, null);
    }

    public static void fillCircle(String layerId, float centerX, float centerY, float radius, Color color, 
                                 BlurEffect blur, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        CircleCommand command = new CircleCommand(centerX, centerY, radius, renderColor, blur, false, 0, 0, 0);
        layer.addCommand(command);
    }

    /**
     * Layered circle stroke rendering with effects
     */
    public static void strokeCircle(String layerId, float centerX, float centerY, float radius, float strokeWidth, Color color) {
        strokeCircle(layerId, centerX, centerY, radius, strokeWidth, color, null, null);
    }

    public static void strokeCircle(String layerId, float centerX, float centerY, float radius, float strokeWidth, 
                                   Color color, BlurEffect blur, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        CircleCommand command = new CircleCommand(centerX, centerY, radius, renderColor, blur, true, strokeWidth, 0, 0);
        layer.addCommand(command);
    }

    /**
     * Layered arc rendering with effects
     */
    public static void fillArc(String layerId, float centerX, float centerY, float radius, 
                              float startAngle, float endAngle, Color color) {
        fillArc(layerId, centerX, centerY, radius, startAngle, endAngle, color, null, null);
    }

    public static void fillArc(String layerId, float centerX, float centerY, float radius, 
                              float startAngle, float endAngle, Color color, BlurEffect blur, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        ArcCommand command = new ArcCommand(centerX, centerY, radius, renderColor, blur, false, 0, startAngle, endAngle);
        layer.addCommand(command);
    }

    /**
     * Layered arc stroke rendering with effects
     */
    public static void strokeArc(String layerId, float centerX, float centerY, float radius, 
                                float startAngle, float endAngle, float strokeWidth, Color color) {
        strokeArc(layerId, centerX, centerY, radius, startAngle, endAngle, strokeWidth, color, null, null);
    }

    public static void strokeArc(String layerId, float centerX, float centerY, float radius, 
                                float startAngle, float endAngle, float strokeWidth, Color color, 
                                BlurEffect blur, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        ArcCommand command = new ArcCommand(centerX, centerY, radius, renderColor, blur, true, strokeWidth, startAngle, endAngle);
        layer.addCommand(command);
    }

    /**
     * Text rendering with advanced typography
     */
    public static void drawText(String layerId, String text, float x, float y, Color color) {
        drawText(layerId, text, x, y, color, null, false, null);
    }

    public static void drawText(String layerId, String text, float x, float y, Color color, 
                               String font, boolean shadow, Animation animation) {
        RenderLayer layer = getLayer(layerId);
        
        Color renderColor = color;
        if (animation != null && animation.isActive()) {
            renderColor = color.withAlpha(color.a * animation.getProgress());
        }
        
        TextCommand command = new TextCommand(text, x, y, renderColor, font, shadow);
        layer.addCommand(command);
    }
    
    /**
     * Direct text rendering - for immediate rendering with DrawContext
     */
    public static void drawText(DrawContext context, String text, float x, float y, Color color, boolean shadow) {
        int intColor = color.getRGBA();
        if (shadow) {
            context.drawText(mc.textRenderer, text, (int)(x + 1), (int)(y + 1), 0xFF000000, false);
        }
        context.drawText(mc.textRenderer, text, (int)x, (int)y, intColor, false);
    }
    
    /**
     * Draw text with DrawContext and TextStyle for UI components
     */
    public static void drawText(DrawContext context, String text, float x, float y, AutreTextRenderer.TextStyle style) {
        AutreTextRenderer.drawText(context, text, x, y, style);
    }
    
    /**
     * Draw text with DrawContext and Color (default style)
     */
    public static void drawText(DrawContext context, String text, float x, float y, Color color) {
        drawText(context, text, x, y, color, false);
    }

    /**
     * Set clipping region for partial rendering
     */
    public static void setClipBounds(float x, float y, float width, float height) {
        clipBounds.set(x, y, width, height);
        clipping = true;
    }

    public static void clearClipBounds() {
        clipping = false;
    }
    
    /**
     * Scissor test for clipping
     */
    public static void enableScissor(int x, int y, int width, int height) {
        // Enable OpenGL scissor test for clipping
        com.mojang.blaze3d.systems.RenderSystem.enableScissor(x, y, width, height);
    }
    
    public static void disableScissor() {
        // Disable OpenGL scissor test
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();
    }

    /**
     * Render lifecycle management
     */
    public static void beginRender() {
        // Initialize rendering state
        // TODO: Setup render batches, clear caches, etc.
    }
    
    public static void endRender() {
        // Finalize rendering
        // TODO: Flush batches, cleanup state, etc.
    }

    /**
     * Animation management
     */
    public static void startAnimation(String id, Animation animation) {
        animation.start();
        activeAnimations.put(id, animation);
    }

    public static void stopAnimation(String id) {
        Animation animation = activeAnimations.get(id);
        if (animation != null) {
            animation.stop();
        }
        activeAnimations.remove(id);
    }

    public static Animation getAnimation(String id) {
        return activeAnimations.get(id);
    }

    /**
     * Clear layer contents
     */
    public static void clearLayer(String layerId) {
        RenderLayer layer = layers.get(layerId);
        if (layer != null) {
            layer.clearCommands();
        }
    }

    public static void clearAllLayers() {
        layers.values().forEach(RenderLayer::clearCommands);
    }

    /**
     * Invalidate region for partial redraw
     */
    public static void invalidateRegion(float x, float y, float width, float height) {
        Rectangle invalidRect = new Rectangle(x, y, width, height);
        
        for (RenderLayer layer : layers.values()) {
            if (layer.getBounds().intersects(invalidRect)) {
                layer.markDirty();
            }
        }
    }

    // Render command implementations
    private static class RectCommand implements RenderCommand {
        private final Rectangle bounds;
        private final Color color;
        private final BlurEffect blur;
        private final boolean rounded;
        
        public RectCommand(float x, float y, float width, float height, Color color, BlurEffect blur, boolean rounded) {
            this.bounds = new Rectangle(x, y, width, height);
            this.color = color;
            this.blur = blur;
            this.rounded = rounded;
        }
        
        @Override
        public void execute(DrawContext context) {
            if (blur != null) {
                // Apply blur effect (simplified implementation)
                executeWithBlur(context);
            } else {
                executeNormal(context);
            }
        }
        
        private void executeNormal(DrawContext context) {
            Matrix4f matrix = currentMatrices.peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            
            buffer.vertex(matrix, bounds.x, bounds.y, 0f).color(color.r, color.g, color.b, color.a);
            buffer.vertex(matrix, bounds.x, bounds.y + bounds.height, 0f).color(color.r, color.g, color.b, color.a);
            buffer.vertex(matrix, bounds.x + bounds.width, bounds.y + bounds.height, 0f).color(color.r, color.g, color.b, color.a);
            buffer.vertex(matrix, bounds.x + bounds.width, bounds.y, 0f).color(color.r, color.g, color.b, color.a);
            
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
        
        private void executeWithBlur(DrawContext context) {
            // Simplified blur - in practice would use framebuffers and gaussian blur shaders
            Color blurredColor = color.withAlpha(color.a * 0.7f);
            
            for (int i = 0; i < blur.getPasses(); i++) {
                float offset = i * blur.getIntensity() / blur.getPasses();
                executeBlurPass(context, blurredColor, offset);
            }
            
            executeNormal(context);
        }
        
        private void executeBlurPass(DrawContext context, Color blurColor, float offset) {
            Matrix4f matrix = currentMatrices.peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            
            float x = bounds.x - offset;
            float y = bounds.y - offset;
            float w = bounds.width + offset * 2;
            float h = bounds.height + offset * 2;
            
            buffer.vertex(matrix, x, y, 0f).color(blurColor.r, blurColor.g, blurColor.b, blurColor.a);
            buffer.vertex(matrix, x, y + h, 0f).color(blurColor.r, blurColor.g, blurColor.b, blurColor.a);
            buffer.vertex(matrix, x + w, y + h, 0f).color(blurColor.r, blurColor.g, blurColor.b, blurColor.a);
            buffer.vertex(matrix, x + w, y, 0f).color(blurColor.r, blurColor.g, blurColor.b, blurColor.a);
            
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
        
        @Override
        public Rectangle getBounds() { return bounds; }
        
        @Override
        public boolean canBatch(RenderCommand other) {
            return other instanceof RectCommand && ((RectCommand) other).blur == this.blur;
        }
    }

    private static class RoundedRectCommand implements RenderCommand {
        private final Rectangle bounds;
        private final float radius;
        private final Color color;
        private final BlurEffect blur;
        
        public RoundedRectCommand(float x, float y, float width, float height, float radius, Color color, BlurEffect blur) {
            this.bounds = new Rectangle(x, y, width, height);
            this.radius = radius;
            this.color = color;
            this.blur = blur;
        }
        
        @Override
        public void execute(DrawContext context) {
            // Simplified rounded rect using triangle fan
            Matrix4f matrix = currentMatrices.peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            
            // Center point
            float centerX = bounds.x + bounds.width / 2f;
            float centerY = bounds.y + bounds.height / 2f;
            buffer.vertex(matrix, centerX, centerY, 0f).color(color.r, color.g, color.b, color.a);
            
            // Generate rounded corners
            int segments = Math.max(4, (int)(radius / 2));
            for (int i = 0; i <= segments * 4; i++) {
                float angle = (float)(i * Math.PI * 2 / (segments * 4));
                float x = centerX + (float)Math.cos(angle) * (bounds.width / 2f - radius);
                float y = centerY + (float)Math.sin(angle) * (bounds.height / 2f - radius);
                buffer.vertex(matrix, x, y, 0f).color(color.r, color.g, color.b, color.a);
            }
            
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
        
        @Override
        public Rectangle getBounds() { return bounds; }
        
        @Override
        public boolean canBatch(RenderCommand other) {
            return other instanceof RoundedRectCommand;
        }
    }

    private static class GradientRectCommand implements RenderCommand {
        private final Rectangle bounds;
        private final Color startColor, endColor;
        private final boolean vertical;
        
        public GradientRectCommand(float x, float y, float width, float height, Color startColor, Color endColor, boolean vertical) {
            this.bounds = new Rectangle(x, y, width, height);
            this.startColor = startColor;
            this.endColor = endColor;
            this.vertical = vertical;
        }
        
        @Override
        public void execute(DrawContext context) {
            Matrix4f matrix = currentMatrices.peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            
            if (vertical) {
                buffer.vertex(matrix, bounds.x, bounds.y, 0f).color(startColor.r, startColor.g, startColor.b, startColor.a);
                buffer.vertex(matrix, bounds.x, bounds.y + bounds.height, 0f).color(endColor.r, endColor.g, endColor.b, endColor.a);
                buffer.vertex(matrix, bounds.x + bounds.width, bounds.y + bounds.height, 0f).color(endColor.r, endColor.g, endColor.b, endColor.a);
                buffer.vertex(matrix, bounds.x + bounds.width, bounds.y, 0f).color(startColor.r, startColor.g, startColor.b, startColor.a);
            } else {
                buffer.vertex(matrix, bounds.x, bounds.y, 0f).color(startColor.r, startColor.g, startColor.b, startColor.a);
                buffer.vertex(matrix, bounds.x, bounds.y + bounds.height, 0f).color(startColor.r, startColor.g, startColor.b, startColor.a);
                buffer.vertex(matrix, bounds.x + bounds.width, bounds.y + bounds.height, 0f).color(endColor.r, endColor.g, endColor.b, endColor.a);
                buffer.vertex(matrix, bounds.x + bounds.width, bounds.y, 0f).color(endColor.r, endColor.g, endColor.b, endColor.a);
            }
            
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
        
        @Override
        public Rectangle getBounds() { return bounds; }
        
        @Override
        public boolean canBatch(RenderCommand other) { return false; }
    }

    private static class TextCommand implements RenderCommand {
        private final String text;
        private final Rectangle bounds;
        private final Color color;
        private final String font;
        private final boolean shadow;
        
        public TextCommand(String text, float x, float y, Color color, String font, boolean shadow) {
            this.text = text;
            this.color = color;
            this.font = font;
            this.shadow = shadow;
            
            // Calculate bounds
            TextRenderer textRenderer = mc.textRenderer;
            float width = textRenderer.getWidth(text);
            float height = textRenderer.fontHeight;
            this.bounds = new Rectangle(x, y, width, height);
        }
        
        @Override
        public void execute(DrawContext context) {
            TextRenderer textRenderer = mc.textRenderer;
            int colorInt = color.getRGBA();
            
            if (shadow) {
                context.drawTextWithShadow(textRenderer, text, (int)bounds.x, (int)bounds.y, colorInt);
            } else {
                context.drawText(textRenderer, text, (int)bounds.x, (int)bounds.y, colorInt, false);
            }
        }
        
        @Override
        public Rectangle getBounds() { return bounds; }
        
        @Override
        public boolean canBatch(RenderCommand other) {
            return other instanceof TextCommand && ((TextCommand) other).font.equals(this.font);
        }
    }

    private static class CircleCommand implements RenderCommand {
        private final Rectangle bounds;
        private final float centerX, centerY, radius;
        private final Color color;
        private final BlurEffect blur;
        private final boolean stroke;
        private final float strokeWidth;
        
        public CircleCommand(float centerX, float centerY, float radius, Color color, BlurEffect blur, 
                           boolean stroke, float strokeWidth, float unused1, float unused2) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            this.color = color;
            this.blur = blur;
            this.stroke = stroke;
            this.strokeWidth = strokeWidth;
            
            // Calculate bounds
            this.bounds = new Rectangle(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        
        @Override
        public void execute(DrawContext context) {
            if (stroke) {
                strokeCircle(currentMatrices, centerX, centerY, radius, strokeWidth, color);
            } else {
                fillCircle(currentMatrices, centerX, centerY, radius, color);
            }
        }
        
        @Override
        public Rectangle getBounds() { return bounds; }
        
        @Override
        public boolean canBatch(RenderCommand other) {
            return other instanceof CircleCommand && ((CircleCommand) other).stroke == this.stroke;
        }
    }

    private static class ArcCommand implements RenderCommand {
        private final Rectangle bounds;
        private final float centerX, centerY, radius;
        private final Color color;
        private final BlurEffect blur;
        private final boolean stroke;
        private final float strokeWidth;
        private final float startAngle, endAngle;
        
        public ArcCommand(float centerX, float centerY, float radius, Color color, BlurEffect blur, 
                         boolean stroke, float strokeWidth, float startAngle, float endAngle) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            this.color = color;
            this.blur = blur;
            this.stroke = stroke;
            this.strokeWidth = strokeWidth;
            this.startAngle = startAngle;
            this.endAngle = endAngle;
            
            // Calculate bounds (simplified - could be more precise for arcs)
            this.bounds = new Rectangle(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        
        @Override
        public void execute(DrawContext context) {
            if (stroke) {
                strokeArc(currentMatrices, centerX, centerY, radius, startAngle, endAngle, strokeWidth, color);
            } else {
                fillArc(currentMatrices, centerX, centerY, radius, startAngle, endAngle, color);
            }
        }
        
        @Override
        public Rectangle getBounds() { return bounds; }
        
        @Override
        public boolean canBatch(RenderCommand other) {
            return other instanceof ArcCommand && ((ArcCommand) other).stroke == this.stroke;
        }
    }

    /**
     * Utility methods
     */
    public static float getTextWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }
    
    public static float getTextHeight() {
        return mc.textRenderer.fontHeight;
    }
    
    public static void drawCenteredText(String layerId, String text, float centerX, float centerY, Color color) {
        float width = getTextWidth(text);
        float height = getTextHeight();
        drawText(layerId, text, centerX - width / 2f, centerY - height / 2f, color);
    }

    /**
     * Performance monitoring
     */
    public static class PerformanceStats {
        private static int drawCalls = 0;
        private static int verticesRendered = 0;
        private static long frameTime = 0;
        
        public static void incrementDrawCalls() { drawCalls++; }
        public static void addVertices(int count) { verticesRendered += count; }
        public static void setFrameTime(long time) { frameTime = time; }
        
        public static int getDrawCalls() { return drawCalls; }
        public static int getVerticesRendered() { return verticesRendered; }
        public static long getFrameTime() { return frameTime; }
        
        public static void reset() {
            drawCalls = 0;
            verticesRendered = 0;
            frameTime = 0;
        }
    }

    /**
     * Debug utilities
     */
    public static void drawDebugBounds(String layerId, Rectangle rect, Color color) {
        // Draw wireframe rectangle
        drawLine(layerId, rect.x, rect.y, rect.x + rect.width, rect.y, color);
        drawLine(layerId, rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height, color);
        drawLine(layerId, rect.x + rect.width, rect.y + rect.height, rect.x, rect.y + rect.height, color);
        drawLine(layerId, rect.x, rect.y + rect.height, rect.x, rect.y, color);
    }
    
    private static void drawLine(String layerId, float x1, float y1, float x2, float y2, Color color) {
        // Simple line implementation
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        float angle = (float)Math.atan2(dy, dx);
        
        // Draw as thin rectangle
        MatrixStack matrices = currentMatrices;
        matrices.push();
        matrices.translate(x1, y1, 0);
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotation(angle));
        
        fillRect(layerId, 0, -0.5f, length, 1f, color);
        
        matrices.pop();
    }
}