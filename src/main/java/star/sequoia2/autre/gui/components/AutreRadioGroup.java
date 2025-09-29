package star.sequoia2.autre.gui.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Radio group for managing exclusive selection among radio buttons
 */
public class AutreRadioGroup {
    protected List<AutreRadioBox> radioButtons;
    protected AutreRadioBox selectedButton;
    protected Runnable onSelectionChange;
    
    public AutreRadioGroup() {
        this.radioButtons = new ArrayList<>();
    }
    
    public AutreRadioGroup addRadioBox(AutreRadioBox radioBox) {
        radioButtons.add(radioBox);
        radioBox.setGroup(this);
        return this;
    }
    
    public AutreRadioGroup removeRadioBox(AutreRadioBox radioBox) {
        radioButtons.remove(radioBox);
        radioBox.setGroup(null);
        if (selectedButton == radioBox) {
            selectedButton = null;
        }
        return this;
    }
    
    public AutreRadioGroup setSelectedButton(AutreRadioBox button) {
        if (radioButtons.contains(button)) {
            // Deselect current
            if (selectedButton != null) {
                selectedButton.selected = false;
            }
            
            // Select new
            selectedButton = button;
            if (button != null) {
                button.selected = true;
            }
            
            if (onSelectionChange != null) {
                onSelectionChange.run();
            }
        }
        return this;
    }
    
    public AutreRadioBox getSelectedButton() {
        return selectedButton;
    }
    
    public Object getSelectedValue() {
        return selectedButton != null ? selectedButton.getValue() : null;
    }
    
    public AutreRadioGroup setOnSelectionChange(Runnable callback) {
        this.onSelectionChange = callback;
        return this;
    }
    
    // Internal method called by radio boxes
    void handleSelectionChange(AutreRadioBox radioBox) {
        if (radioBox.isSelected()) {
            setSelectedButton(radioBox);
        }
    }
    
    public List<AutreRadioBox> getRadioButtons() {
        return new ArrayList<>(radioButtons);
    }
}