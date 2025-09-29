package star.sequoia2.autre.gui.events;

/**
 * Base class for all GUI events
 */
public abstract class AutreGuiEvent {
    private boolean cancelled = false;
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}