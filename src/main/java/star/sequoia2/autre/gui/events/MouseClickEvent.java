package star.sequoia2.autre.gui.events;

/**
 * Mouse click events for GUI components
 */
public class MouseClickEvent extends AutreGuiEvent {
    public final float x, y;
    public final int button;
    public final boolean pressed; // true for press, false for release
    
    public MouseClickEvent(float x, float y, int button, boolean pressed) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.pressed = pressed;
    }
    
    public boolean isPressed() {
        return pressed;
    }
    
    public boolean isReleased() {
        return !pressed;
    }
    
    public boolean isLeftClick() {
        return button == 0;
    }
    
    public boolean isRightClick() {
        return button == 1;
    }
    
    public boolean isMiddleClick() {
        return button == 2;
    }
}