package star.sequoia2.autre.gui.events;

/**
 * Mouse hover event for GUI components
 */
public class GuiHoverEvent implements GuiEvent {
    private final float mouseX, mouseY;
    private final boolean isHovering;
    private boolean consumed = false;
    
    public GuiHoverEvent(float mouseX, float mouseY, boolean isHovering) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.isHovering = isHovering;
    }
    
    public float getMouseX() { return mouseX; }
    public float getMouseY() { return mouseY; }
    public boolean isHovering() { return isHovering; }
    
    @Override
    public boolean isConsumed() { return consumed; }
    
    @Override
    public void consume() { consumed = true; }
}