package star.sequoia2.autre.gui.events;

/**
 * Mouse hover events for GUI components
 */
public class MouseHoverEvent extends AutreGuiEvent {
    public final float x, y;
    public final boolean entered; // true when mouse enters, false when leaves
    
    public MouseHoverEvent(float x, float y, boolean entered) {
        this.x = x;
        this.y = y;
        this.entered = entered;
    }
    
    public boolean isEntered() {
        return entered;
    }
    
    public boolean isExited() {
        return !entered;
    }
}