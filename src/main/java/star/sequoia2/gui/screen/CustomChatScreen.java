package star.sequoia2.gui.screen;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import star.sequoia2.accessors.FeaturesAccessor;

import static star.sequoia2.client.SeqClient.mc;

public class CustomChatScreen extends Screen implements FeaturesAccessor {

    @Getter
    private MCEFBrowser browser;

    public CustomChatScreen() {
        super(Text.of("CustomChat"));
    }

    public float getScale() {
        float base = 1.0f;
        float sf = (float) (mc != null ? mc.getWindow().getScaleFactor() : base);
        return base / sf;
    }

    public void ensureReady() {
        if (browser == null) {
            String path = getClass().getResource("/assets/seq/chat/chat.html").toExternalForm();
            browser = MCEF.createBrowser(path, true);
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();
            int scaledW = Math.max(1, Math.round(w / getScale()));
            int scaledH = Math.max(1, Math.round(h / getScale()));
            browser.resize(scaledW, scaledH);
        }
    }

    @Override
    protected void init() {
        super.init();
        ensureReady();
        int scaledW = Math.max(1, Math.round(width / getScale()));
        int scaledH = Math.max(1, Math.round(height / getScale()));
        browser.resize(scaledW, scaledH);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderBrowser(context, 0, 0, this.width, this.height);
    }

    public void renderBrowser(DrawContext context, int x, int y, int w, int h) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, x, y + h, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255);
        buffer.vertex(matrix, x + w, y + h, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255);
        buffer.vertex(matrix, x + w, y, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255);
        buffer.vertex(matrix, x, y, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
    }

    private int toLocalX(double mouseX) {
        double local = (mouseX) / getScale();
        double max = (double) width / getScale();
        return (int) Math.max(0, Math.min(Math.round(max), Math.round(local)));
    }

    private int toLocalY(double mouseY) {
        double local = (mouseY) / getScale();
        double max = (double) height / getScale();
        return (int) Math.max(0, Math.min(Math.round(max), Math.round(local)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 3) { if (client != null) client.setScreen(null); return true; }
        if (browser != null) {
            browser.sendMousePress(toLocalX(mouseX), toLocalY(mouseY), button);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (browser != null) {
            browser.sendMouseRelease(toLocalX(mouseX), toLocalY(mouseY), button);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (browser != null) browser.sendMouseMove(toLocalX(mouseX), toLocalY(mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hx, double vy) {
        if (browser != null) {
            browser.sendMouseWheel(toLocalX(mouseX), toLocalY(mouseY), vy, 0);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, hx, vy);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (browser != null && keyCode != net.minecraft.client.util.InputUtil.GLFW_KEY_ESCAPE) {
            browser.sendKeyPress(keyCode, 0L, 0);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (browser != null) {
            browser.sendKeyRelease(keyCode, 0L, 0);
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (browser != null) {
            browser.sendKeyTyped(codePoint, 0);
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}
