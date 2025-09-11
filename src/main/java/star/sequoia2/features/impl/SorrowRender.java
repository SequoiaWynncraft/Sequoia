package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.events.PacketEvent;
import star.sequoia2.events.Render3DEvent;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.settings.types.BooleanSetting;
import star.sequoia2.settings.types.ColorSetting;
import star.sequoia2.utils.Timer;
import star.sequoia2.utils.render.TextureStorage;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

import static star.sequoia2.client.SeqClient.mc;

public class SorrowRender extends ToggleFeature implements RenderUtilAccessor {

    ColorSetting color = settings().color("Color", "color of the sorrow", new mil.nga.color.Color(255, 0, 0));

    BooleanSetting sneak = settings().bool("CheckSneaking", "toggle to check sneaking to detect sorrow", false);

    private final Timer timer;
    private final Timer sorrowTimer;

    public SorrowRender() {
        super("SorrowRender", "Custom visuals for blood sorrow");
        timer = new Timer();
        sorrowTimer = new Timer();
        timer.reset();
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (mc.player != null && event.packet() instanceof PlaySoundS2CPacket soundPacket && soundPacket.getSound().value() == SoundEvents.ENTITY_WITHER_SHOOT && (!sneak.get() || mc.player.isSneaking())) {
            SeqClient.info(soundPacket.getSound().value().toString());
            sorrowTimer.reset();
        }
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (sorrowTimer.passed(1600L)) return;

        float delta = render3DUtil().getTickDelta();

        Vec3d stomach = render3DUtil().lerp(mc.player.getLastRenderPos().add(0, 1, 0), mc.player.getPos().add(0, 1, 0), delta);

        Vec3d forward = mc.player.getRotationVec(delta).normalize();

        Vec3d upWorld = new Vec3d(0, 1, 0);

        float yawDeg = mc.player.getYaw(delta);
        double yaw = Math.toRadians(yawDeg);
        Vec3d forwardFlat = new Vec3d(-Math.sin(yaw), 0, Math.cos(yaw));
        Vec3d right = upWorld.crossProduct(forwardFlat).normalize();
        Vec3d up = upWorld;

        double side = 0;
        double height = mc.player.getHeight() - 1.5;
        double ahead = 0.01;

        if (mc.player.getMainArm() == Arm.RIGHT) side = -side;

        Vec3d start = stomach
                .add(right.multiply(side))
                .add(up.multiply(height))
                .add(forwardFlat.multiply(ahead));

        Vec3d end = start.add(forward.multiply(20.0));

        render3DUtil().drawLine(event.matrices(), start, end, color.get(), 4f, true);

        final int ringCount = 6;
        final float baseRadius = 0.1f;
        for (int i = 0; i < ringCount; i++) {
            float phaseOffset = i / (float) ringCount;
            drawCircle(event, start, end, baseRadius, phaseOffset);
        }
    }

    private void drawCircle(Render3DEvent event, Vec3d start, Vec3d end, float baseRadius, float phaseOffset) {
        final float segStart = 0.02f, segEnd = 0.80f;
        final float segLen   = segEnd - segStart;
        final float segMid   = (segStart + segEnd) * 0.5f;
        final float segHalf  = segLen * 0.5f;

        float seconds = timer.getPassed() / 1500.0f;
        final float cyclesPerSecond = 0.6f;
        float slide01 = (seconds * cyclesPerSecond + phaseOffset) % 1.0f;
        float tOnLine = segStart + slide01 * segLen;

        float distNorm = Math.abs(tOnLine - segMid) / segHalf;
        float grow     = 1.0f - (float) Math.pow(distNorm, 2.0);
        final float extraRadius = 0.22f;
        float radius = baseRadius + extraRadius * grow;

        Vec3d axis   = end.subtract(start).normalize();
        Vec3d center = start.lerp(end, tOnLine);
        Vec3d ref = Math.abs(axis.dotProduct(new Vec3d(0, 1, 0))) > 0.99 ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);
        Vec3d u = axis.crossProduct(ref).normalize();
        Vec3d v = axis.crossProduct(u).normalize();

        float spinRad = (float) (seconds * Math.PI * 2.0 * 0.35);
        double cs = Math.cos(spinRad), sn = Math.sin(spinRad);
        Vec3d ur = new Vec3d(u.x * cs - v.x * sn, u.y * cs - v.y * sn, u.z * cs - v.z * sn);
        Vec3d vr = new Vec3d(u.x * sn + v.x * cs, u.y * sn + v.y * cs, u.z * sn + v.z * cs);

        Vec3d p0 = center.add(ur.multiply(-radius)).add(vr.multiply( radius));
        Vec3d p1 = center.add(ur.multiply( radius)).add(vr.multiply( radius));
        Vec3d p2 = center.add(ur.multiply( radius)).add(vr.multiply(-radius));
        Vec3d p3 = center.add(ur.multiply(-radius)).add(vr.multiply(-radius));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, TextureStorage.circle);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        BufferBuilder buffer = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_TEXTURE_COLOR
        );

        MatrixStack matrices = event.matrices();
        matrices.push();

        Matrix4f mat = matrices.peek().getPositionMatrix();
        Vec3d cam = mc.getEntityRenderDispatcher().camera.getPos();
        int packed = new Color(color.get().getRed(), color.get().getGreen(), color.get().getBlue(), color.get().getAlpha()).getRGB();

        buffer.vertex(mat, (float)(p0.x - cam.x), (float)(p0.y - cam.y), (float)(p0.z - cam.z)).texture(0, 1).color(packed);
        buffer.vertex(mat, (float)(p1.x - cam.x), (float)(p1.y - cam.y), (float)(p1.z - cam.z)).texture(1, 1).color(packed);
        buffer.vertex(mat, (float)(p2.x - cam.x), (float)(p2.y - cam.y), (float)(p2.z - cam.z)).texture(1, 0).color(packed);
        buffer.vertex(mat, (float)(p3.x - cam.x), (float)(p3.y - cam.y), (float)(p3.z - cam.z)).texture(0, 0).color(packed);

        matrices.pop();

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}
