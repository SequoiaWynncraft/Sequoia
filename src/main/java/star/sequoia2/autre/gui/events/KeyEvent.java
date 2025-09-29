package star.sequoia2.autre.gui.events;

/**
 * Keyboard events for GUI components
 */
public class KeyEvent extends AutreGuiEvent {
    public final int keyCode;
    public final int scanCode;
    public final int modifiers;
    public final boolean pressed;
    
    public KeyEvent(int keyCode, int scanCode, int modifiers, boolean pressed) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
        this.pressed = pressed;
    }
    
    public boolean isPressed() {
        return pressed;
    }
    
    public boolean isReleased() {
        return !pressed;
    }
    
    public boolean hasShift() {
        return (modifiers & 1) != 0;
    }
    
    public boolean hasCtrl() {
        return (modifiers & 2) != 0;
    }
    
    public boolean hasAlt() {
        return (modifiers & 4) != 0;
    }
}