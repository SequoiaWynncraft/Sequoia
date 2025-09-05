package star.sequoia2.gui.component;

public abstract class InteractableComponent extends Component{
    public abstract void mouseMoved(float mouseX, float mouseY);

    public abstract void mouseClicked(float mouseX, float mouseY, int button);

    public abstract void mouseReleased(float mouseX, float mouseY, int button);

    public abstract void mouseScrolled(float mouseX, float mouseY, double horizontalAmount, double verticalAmount);

    public abstract void keyPressed(int keyCode, int scanCode, int modifiers);

    public abstract void keyReleased(int keyCode, int scanCode, int modifiers);

    public abstract void charTyped(char chr, int modifiers);
}
