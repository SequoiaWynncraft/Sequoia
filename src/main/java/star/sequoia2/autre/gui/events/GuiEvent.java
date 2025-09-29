package star.sequoia2.autre.gui.events;

/**
 * Base interface for all GUI events
 */
public interface GuiEvent {
    boolean isConsumed();
    void consume();
}