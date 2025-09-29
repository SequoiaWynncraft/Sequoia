package star.sequoia2.autre.gui.events;

/**
 * Mouse click event for GUI components
 */
public class GuiClickEvent implements GuiEvent {
    private final float mouseX, mouseY;
    private final int button;
    private boolean consumed = false;
    
    public GuiClickEvent(float mouseX, float mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }
    
    public float getMouseX() { return mouseX; }
    public float getMouseY() { return mouseY; }
    public int getButton() { return button; }
    
    @Override
    public boolean isConsumed() { return consumed; }
    
    @Override
    public void consume() { consumed = true; }
}