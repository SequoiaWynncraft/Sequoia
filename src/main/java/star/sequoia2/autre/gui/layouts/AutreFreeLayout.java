package star.sequoia2.autre.gui.layouts;

import star.sequoia2.autre.gui.containers.AutreContainer;

/**
 * Free layout allows manual positioning of components
 */
public class AutreFreeLayout extends AutreLayout {
    
    public AutreFreeLayout(AutreContainer container) {
        super(container);
    }
    
    @Override
    public void layoutComponents() {
        // Free layout doesn't automatically position components
        // Components maintain their manually set positions
    }
}