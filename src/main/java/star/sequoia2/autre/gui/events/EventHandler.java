package star.sequoia2.autre.gui.events;

/**
 * Event handler interface for GUI components
 */
@FunctionalInterface
public interface EventHandler<T extends AutreGuiEvent> {
    void handle(T event);
}