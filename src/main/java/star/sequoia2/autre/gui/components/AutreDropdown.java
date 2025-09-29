package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.ArrayList;
import java.util.List;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Flat dropdown/select component
 */
public class AutreDropdown extends AutreComponent {
    protected List<String> options;
    protected int selectedIndex = -1;
    protected boolean isOpen = false;
    protected String placeholder = "Select...";
    
    protected float optionHeight = 25f;
    protected float maxDropdownHeight = 150f;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.getAccent().withAlpha(0.3f);
    protected AutreRenderer2.Color hoverColor = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color selectedColor = AutreRenderer2.Color.getAccent();
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color placeholderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
    
    protected Runnable onSelectionChange;
    
    public AutreDropdown(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.options = new ArrayList<>();
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreDropdown addOption(String option) {
        options.add(option);
        return this;
    }
    
    public AutreDropdown setOptions(List<String> options) {
        this.options = new ArrayList<>(options);
        return this;
    }
    
    public AutreDropdown setSelectedIndex(int index) {
        if (index >= -1 && index < options.size()) {
            this.selectedIndex = index;
            if (onSelectionChange != null) {
                onSelectionChange.run();
            }
        }
        return this;
    }
    
    public AutreDropdown setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public String getSelectedOption() {
        return selectedIndex >= 0 && selectedIndex < options.size() ? options.get(selectedIndex) : null;
    }
    
    public AutreDropdown setOnSelectionChange(Runnable callback) {
        this.onSelectionChange = callback;
        return this;
    }
    
    public void close() {
        isOpen = false;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        if (isOpen) {
            // Check if clicking on dropdown options
            float dropdownY = getAbsoluteY() + height;
            float dropdownHeight = Math.min(options.size() * optionHeight, maxDropdownHeight);
            
            if (event.y >= dropdownY && event.y <= dropdownY + dropdownHeight &&
                event.x >= getAbsoluteX() && event.x <= getAbsoluteX() + width) {
                
                // Calculate which option was clicked
                int optionIndex = (int) ((event.y - dropdownY) / optionHeight);
                if (optionIndex >= 0 && optionIndex < options.size()) {
                    setSelectedIndex(optionIndex);
                }
            }
            close();
        } else {
            // Check if clicking on dropdown button
            if (event.x >= getAbsoluteX() && event.x <= getAbsoluteX() + width &&
                event.y >= getAbsoluteY() && event.y <= getAbsoluteY() + height) {
                isOpen = true;
            }
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        // Main dropdown button - flat design
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, height, backgroundColor);
        
        // Border
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), width, 1f, borderColor);
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY() + height - 1f, width, 1f, borderColor);
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX(), getAbsoluteY(), 1f, height, borderColor);
        AutreRenderer2.fillRect(context.getMatrices(),
            getAbsoluteX() + width - 1f, getAbsoluteY(), 1f, height, borderColor);
        
        // Text
        String displayText;
        AutreRenderer2.Color displayTextColor;
        
        if (selectedIndex >= 0 && selectedIndex < options.size()) {
            displayText = options.get(selectedIndex);
            displayTextColor = textColor;
        } else {
            displayText = placeholder;
            displayTextColor = placeholderColor;
        }
        
        float textX = getAbsoluteX() + 8f;
        float textY = getAbsoluteY() + (height - mc.textRenderer.fontHeight) / 2f;
        AutreRenderer2.drawText(context, displayText, textX, textY, displayTextColor, false);
        
        // Arrow indicator
        float arrowX = getAbsoluteX() + width - 20f;
        float arrowY = getAbsoluteY() + (height - mc.textRenderer.fontHeight) / 2f;
        String arrow = isOpen ? "▲" : "▼";
        AutreRenderer2.drawText(context, arrow, arrowX, arrowY, textColor, false);
        
        // Dropdown options if open
        if (isOpen && !options.isEmpty()) {
            float dropdownY = getAbsoluteY() + height;
            float dropdownHeight = Math.min(options.size() * optionHeight, maxDropdownHeight);
            
            // Dropdown background
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), dropdownY, width, dropdownHeight, backgroundColor);
            
            // Dropdown border
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), dropdownY, width, 1f, borderColor);
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), dropdownY + dropdownHeight - 1f, width, 1f, borderColor);
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX(), dropdownY, 1f, dropdownHeight, borderColor);
            AutreRenderer2.fillRect(context.getMatrices(),
                getAbsoluteX() + width - 1f, dropdownY, 1f, dropdownHeight, borderColor);
            
            // Options
            for (int i = 0; i < options.size(); i++) {
                float optionY = dropdownY + i * optionHeight;
                boolean isHovered = mouseX >= getAbsoluteX() && mouseX <= getAbsoluteX() + width &&
                                   mouseY >= optionY && mouseY <= optionY + optionHeight;
                
                // Option background
                if (isHovered) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        getAbsoluteX(), optionY, width, optionHeight, hoverColor);
                } else if (i == selectedIndex) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        getAbsoluteX(), optionY, width, optionHeight, selectedColor.withAlpha(0.2f));
                }
                
                // Option text
                float optionTextX = getAbsoluteX() + 8f;
                float optionTextY = optionY + (optionHeight - mc.textRenderer.fontHeight) / 2f;
                AutreRenderer2.drawText(context, options.get(i), optionTextX, optionTextY, textColor, false);
            }
        }
    }

}