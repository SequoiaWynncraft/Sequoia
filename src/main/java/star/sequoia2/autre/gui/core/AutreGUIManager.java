package star.sequoia2.autre.gui.core;

import net.minecraft.client.MinecraftClient;
import star.sequoia2.autre.gui.components.AutreButton;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * GUI Manager for creating and managing GUI instances
 */
public class AutreGUIManager {
    private static AutreGUI testGui = null;
    
    /**
     * Create a test GUI with a solid color background and a button
     */
    public static void createTestGUI() {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Create a GUI window positioned at center of screen
        float guiWidth = 300f;
        float guiHeight = 200f;
        float screenWidth = mc.getWindow().getScaledWidth();
        float screenHeight = mc.getWindow().getScaledHeight();
        float guiX = (screenWidth - guiWidth) / 2f;
        float guiY = (screenHeight - guiHeight) / 2f;
        
        testGui = new AutreGUI(guiX, guiY, guiWidth, guiHeight);
        testGui.setBackgroundColor(AutreRenderer2.Color.BACKGROUND);
        testGui.setCornerRadius(12f);
        
        // Add a test button with proper constructor
        AutreButton testButton = new AutreButton(50f, 75f, 200f, 40f, "Click Me!");
        
        // Add click handler
        testButton.onClickStart(event -> {
            System.out.println("Autre GUI Button clicked!");
        });
        
        testGui.addComponent(testButton);
    }
    
    /**
     * Show the test GUI
     */
    public static void showTestGUI() {
        if (testGui == null) {
            createTestGUI();
        }
        testGui.show();
    }
    
    /**
     * Hide the test GUI
     */
    public static void hideTestGUI() {
        if (testGui != null) {
            testGui.hide();
        }
    }
    
    /**
     * Toggle the test GUI visibility
     */
    public static void toggleTestGUI() {
        if (testGui == null) {
            createTestGUI();
            testGui.show();
        } else {
            if (testGui.isVisible()) {
                testGui.hide();
            } else {
                testGui.show();
            }
        }
    }
    
    /**
     * Get the test GUI instance
     */
    public static AutreGUI getTestGUI() {
        return testGui;
    }
    
    /**
     * Check if test GUI is visible
     */
    public static boolean isTestGUIVisible() {
        return testGui != null && testGui.isVisible();
    }
}