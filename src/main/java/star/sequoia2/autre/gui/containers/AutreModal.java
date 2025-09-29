package star.sequoia2.autre.gui.containers;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.render.AutreRenderer2;

/**
 * Modal dialog for overlay content with backdrop
 */
public class AutreModal extends AutreContainer {
    protected boolean showBackdrop = true;
    protected AutreRenderer2.Color backdropColor = AutreRenderer2.Color.BLACK.withAlpha(0.5f);
    protected boolean closeOnBackdropClick = true;
    protected Runnable onClose;
    
    public AutreModal(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        // Modal styling - flat with elevation appearance
        setBackgroundColor(AutreRenderer2.Color.SURFACE);
        setBorderWidth(1f);
        setBorderColor(AutreRenderer2.Color.getAccent().withAlpha(0.3f));
    }
    
    public AutreModal setShowBackdrop(boolean show) {
        this.showBackdrop = show;
        return this;
    }
    
    public AutreModal setBackdropColor(AutreRenderer2.Color color) {
        this.backdropColor = color;
        return this;
    }
    
    public AutreModal setCloseOnBackdropClick(boolean close) {
        this.closeOnBackdropClick = close;
        return this;
    }
    
    public AutreModal setOnClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }
    
    public void show() {
        setVisible(true);
        setEnabled(true);
    }
    
    public void hide() {
        setVisible(false);
        setEnabled(false);
        if (onClose != null) {
            onClose.run();
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Render backdrop if enabled
        if (showBackdrop) {
            // Get screen dimensions for full backdrop
            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();
            
            AutreRenderer2.fillRect(context.getMatrices(),
                0, 0, screenWidth, screenHeight, backdropColor);
        }
        
        // Render modal content
        super.renderSelf(context, mouseX, mouseY, deltaTime);
    }
    
    @Override
    public boolean handleMouseClick(float mouseX, float mouseY, int button, boolean pressed) {
        if (!visible || !enabled) return false;
        
        // Check if click is on backdrop
        boolean clickedOnModal = mouseX >= getAbsoluteX() && mouseX <= getAbsoluteX() + width &&
                                mouseY >= getAbsoluteY() && mouseY <= getAbsoluteY() + height;
        
        if (!clickedOnModal && closeOnBackdropClick && pressed) {
            hide();
            return true;
        }
        
        return super.handleMouseClick(mouseX, mouseY, button, pressed);
    }
}