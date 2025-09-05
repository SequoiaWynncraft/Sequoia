package star.sequoia2.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mil.nga.color.Color;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import static star.sequoia2.client.NectarClient.mc;

public class Render3DUtil {
    public void drawBoxFilled(MatrixStack stack, Box box, Color color) {

        java.awt.Color c = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        setup3D();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB());
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        clean3D();
    }

    public void drawBoxFilled(MatrixStack stack, Vec3d vec, Color c) {
        drawBoxFilled(stack, Box.from(vec), c);
    }

    public void drawBoxFilled(MatrixStack stack, BlockPos bp, Color c) {
        drawBoxFilled(stack, new Box(bp), c);
    }

    public void drawBox(MatrixStack stack, Box box, Color color, double lineWidth) {
        java.awt.Color c = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        Vec3d cam = mc.getEntityRenderDispatcher().camera.getPos();
        float minX = (float) (box.minX - cam.x);
        float minY = (float) (box.minY - cam.y);
        float minZ = (float) (box.minZ - cam.z);
        float maxX = (float) (box.maxX - cam.x);
        float maxY = (float) (box.maxY - cam.y);
        float maxZ = (float) (box.maxZ - cam.z);

        setup3D();
        RenderSystem.lineWidth((float) Math.max(1.0, lineWidth));
        // IMPORTANT: use the line shader
        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferBuilder = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        // If you use WorldRenderer/VertexRendering helper it will emit normals as needed
        VertexRendering.drawBox(
                stack, bufferBuilder,
                minX, minY, minZ, maxX, maxY, maxZ,
                c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f
        );

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        clean3D();
    }

    public void drawBox(MatrixStack stack, Vec3d vec, Color c, double lineWidth) {
        drawBox(stack, Box.from(vec), c, lineWidth);
    }

    public void drawBox(MatrixStack stack, BlockPos bp, Color c, double lineWidth) {
        drawBox(stack, new Box(bp), c, lineWidth);
    }

    public void drawLine(MatrixStack matrices, Vec3d start, Vec3d end, Color c, double lineWidth) {
        drawLine(matrices, start, end, c, lineWidth, false);
    }

    public void drawLine(MatrixStack matrices, Vec3d start, Vec3d end, Color c, double lineWidth, boolean depth) {
        setup3D();
        RenderSystem.depthMask(true);
        if (depth) {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth((float) Math.max(1.0, lineWidth));


        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

        float red   = c.getRed() / 255f;
        float green = c.getGreen() / 255f;
        float blue  = c.getBlue() / 255f;
        float alpha = c.getAlpha() / 255f;

        float normalX = 0.0f, normalY = 1.0f, normalZ = 0.0f;

        Vec3d camPos = mc.getEntityRenderDispatcher().camera.getPos();

        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)(start.x - camPos.x), (float)(start.y - camPos.y), (float)(start.z - camPos.z))
                .normal(normalX, normalY, normalZ)
                .color(red, green, blue, alpha);

        bufferBuilder.vertex(matrices.peek().getPositionMatrix(), (float)(end.x - camPos.x), (float)(end.y - camPos.y), (float)(end.z - camPos.z))
                .normal(normalX, normalY, normalZ)
                .color(red, green, blue, alpha);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        clean3D();
    }

    public float getTickDelta() {
        return mc.getRenderTickCounter().getTickDelta(true);
    }

    public Vec3d lerp(Vec3d old, Vec3d current, float delta) {
        double x = old.x + (current.x - old.x) * delta;
        double y = old.y + (current.y - old.y) * delta;
        double z = old.z + (current.z - old.z) * delta;
        return new Vec3d(x, y, z);
    }

    public void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void setup3D() {
        setup();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }

    public void clean() {
        RenderSystem.disableBlend();
    }

    public void clean3D() {
        clean();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}
