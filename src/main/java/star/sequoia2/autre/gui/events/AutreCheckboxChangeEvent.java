package star.sequoia2.autre.gui.events;

import star.sequoia2.autre.gui.components.AutreCheckbox;

/**
 * Event fired when a checkbox state changes
 */
public class AutreCheckboxChangeEvent extends AutreGuiEvent {
    public final AutreCheckbox checkbox;
    public final boolean checked;
    
    public AutreCheckboxChangeEvent(AutreCheckbox checkbox, boolean checked) {
        this.checkbox = checkbox;
        this.checked = checked;
    }
}