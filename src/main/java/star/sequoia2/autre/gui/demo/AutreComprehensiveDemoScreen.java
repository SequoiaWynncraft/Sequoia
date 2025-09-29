package star.sequoia2.autre.gui.demo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Screen wrapper for the comprehensive Autre component demo
 */
public class AutreComprehensiveDemoScreen extends Screen {
    
    private AutreComprehensiveDemoGUI demoGUI;
    
    public AutreComprehensiveDemoScreen() {
        super(Text.literal("Comprehensive Autre Component Demo"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Create the comprehensive demo GUI
        demoGUI = new AutreComprehensiveDemoGUI();
        
        // Center the demo GUI on screen
        float centerX = (width - demoGUI.getWidth()) / 2f;
        float centerY = (height - demoGUI.getHeight()) / 2f;
        demoGUI.setPosition(centerX, centerY);
    }
    
        @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw a solid dark background - no gradient for flat design
        context.fill(0, 0, this.width, this.height, 0xFF1a1a1a);
        
        if (demoGUI != null) {
            demoGUI.render(context, mouseX, mouseY, delta);
        }
        
        // Don't call super.render to avoid default screen gradients
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (demoGUI != null) {
            // Adjust mouse coordinates relative to GUI position
            float relativeX = (float) mouseX - demoGUI.getX();
            float relativeY = (float) mouseY - demoGUI.getY();
            return demoGUI.handleMouseClick(relativeX, relativeY, button, true);
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (demoGUI != null) {
            // Adjust mouse coordinates relative to GUI position
            float relativeX = (float) mouseX - demoGUI.getX();
            float relativeY = (float) mouseY - demoGUI.getY();
            return demoGUI.handleMouseClick(relativeX, relativeY, button, false);
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (demoGUI != null) {
            // Check if mouse is over the demo GUI before scrolling
            float relativeX = (float) mouseX - demoGUI.getX();
            float relativeY = (float) mouseY - demoGUI.getY();
            if (relativeX >= 0 && relativeX <= demoGUI.getWidth() && 
                relativeY >= 0 && relativeY <= demoGUI.getHeight()) {
                return demoGUI.mouseScrolled(mouseX, mouseY, verticalAmount);
            }
        }
        
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // Mouse hover handling is done automatically by the component system
        super.mouseMoved(mouseX, mouseY);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close on ESC
        if (keyCode == 256) { // GLFW_KEY_ESCAPE
            this.close();
            return true;
        }
        
        if (demoGUI != null) {
            return demoGUI.handleKeyEvent(keyCode, scanCode, modifiers, true);
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Don't render the default blurred background - render a light semi-transparent overlay instead
        context.fill(0, 0, this.width, this.height, 0x60000000); // Lighter semi-transparent black
        
        // Add a subtle gradient for better visual depth
        context.fillGradient(0, 0, this.width, this.height / 4, 0x40ffffff, 0x00ffffff);
    }
    
    @Override
    public void close() {
        super.close();
        mc.setScreen(null);
    }
}