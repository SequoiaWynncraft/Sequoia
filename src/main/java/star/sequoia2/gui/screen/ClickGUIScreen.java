package star.sequoia2.gui.screen;

import lombok.Setter;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.gui.categories.Categories;
import star.sequoia2.gui.component.ScissorStack;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static star.sequoia2.client.SeqClient.mc;

public class ClickGUIScreen extends Screen implements FeaturesAccessor, RenderUtilAccessor {

    public static int MOUSE_X;
    public static int MOUSE_Y;
    public static boolean MOUSE_RIGHT_CLICK;
    public static boolean MOUSE_RIGHT_HOLD;
    public static boolean MOUSE_LEFT_CLICK;
    public static boolean MOUSE_LEFT_HOLD;
    public static final ScissorStack SCISSOR_STACK = new ScissorStack();

    @Setter
    private boolean closeOnEscape = true;

    @Getter
    public final GuiRoot root;

    public ClickGUIScreen() {
        super(Text.literal("Seq"));
        root = new GuiRoot(Categories.all().toList());
    }

    private double[] getScaledMouse(double mouseX, double mouseY) {
        float base = 2.0f;
        float sf = (float) mc.getWindow().getScaleFactor();
        float scale = base / sf;
        return new double[]{ mouseX / scale, mouseY / scale };
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        double[] scaled = getScaledMouse(mouseX, mouseY);
        mouseX = scaled[0];
        mouseY = scaled[1];
        if (root != null) root.mouseMoved((float) mouseX, (float) mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        double[] scaled = getScaledMouse(mouseX, mouseY);
        mouseX = scaled[0];
        mouseY = scaled[1];
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            MOUSE_LEFT_CLICK = true;
            MOUSE_LEFT_HOLD = true;
        } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            MOUSE_RIGHT_CLICK = true;
            MOUSE_RIGHT_HOLD = true;
        }
        if (root != null) root.mouseClicked((float) mouseX, (float) mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double[] scaled = getScaledMouse(mouseX, mouseY);
        mouseX = scaled[0];
        mouseY = scaled[1];
        if (button == 0) {
            MOUSE_LEFT_HOLD = false;
        } else if (button == 1) {
            MOUSE_RIGHT_HOLD = false;
        }
        if (root != null) root.mouseReleased((float) mouseX, (float) mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double[] scaled = getScaledMouse(mouseX, mouseY);
        mouseX = scaled[0];
        mouseY = scaled[1];
        if (root != null) root.mouseScrolled((float) mouseX, (float) mouseY, horizontalAmount, verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        double[] scaled = getScaledMouse(mouseX, mouseY);
        int fixedMouseX = (int) scaled[0];
        int fixedMouseY = (int) scaled[1];

        context.getMatrices().push();
        float base = 2.0f;
        float sf = (float) mc.getWindow().getScaleFactor();
        float scale = base / sf;
        float scaledHeight = (context.getScaledWindowHeight() / scale);
        float scaledWidth = (context.getScaledWindowWidth() / scale);
        context.getMatrices().scale(scale, scale, 1.0f);

        if (root != null) {
            root.setPos(0, 0);
            root.setDimensions(scaledWidth, scaledHeight);
            root.layout(scaledWidth, scaledHeight);
            root.render(context, fixedMouseX, fixedMouseY, delta);
        }

        MOUSE_LEFT_CLICK = false;
        MOUSE_RIGHT_CLICK = false;
        MOUSE_X = fixedMouseX;
        MOUSE_Y = fixedMouseY;
        context.getMatrices().pop();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (root != null) root.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (root != null) root.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (root != null) root.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
        MOUSE_LEFT_CLICK = false;
        MOUSE_LEFT_HOLD = false;
        MOUSE_RIGHT_CLICK = false;
        MOUSE_RIGHT_HOLD = false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return closeOnEscape;
    }
}
