package star.sequoia2.autre.gui.components;

import net.minecraft.client.gui.DrawContext;
import star.sequoia2.autre.gui.events.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.util.*;
import java.util.function.Consumer;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Multi-selection dropdown with flat design and checkboxes
 */
public class AutreDropdownMultiple extends AutreComponent {
    protected List<DropdownOption> options = new ArrayList<>();
    protected Set<String> selectedValues = new HashSet<>();
    protected boolean isOpen = false;
    protected String placeholder = "Select options...";
    protected float maxDropdownHeight = 120f;
    protected float itemHeight = 20f;
    
    // Colors for flat design
    protected AutreRenderer2.Color backgroundColor = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color borderColor = AutreRenderer2.Color.SECONDARY;
    protected AutreRenderer2.Color textColor = AutreRenderer2.Color.TEXT_PRIMARY;
    protected AutreRenderer2.Color placeholderColor = AutreRenderer2.Color.TEXT_PRIMARY.darker(0.5f);
    protected AutreRenderer2.Color dropdownBg = AutreRenderer2.Color.SURFACE;
    protected AutreRenderer2.Color hoverColor = AutreRenderer2.Color.getAccent().withAlpha(0.1f);
    protected AutreRenderer2.Color selectedColor = AutreRenderer2.Color.getAccent().withAlpha(0.2f);
    protected AutreRenderer2.Color checkboxColor = AutreRenderer2.Color.getAccent();
    
    protected Consumer<Set<String>> onSelectionChange;
    
    public AutreDropdownMultiple(float x, float y, float width, float height) {
        super(x, y, width, height);
        
        addEventListener(MouseClickEvent.class, this::handleClick);
    }
    
    public AutreDropdownMultiple addOption(String value, String label) {
        options.add(new DropdownOption(value, label));
        return this;
    }
    
    public AutreDropdownMultiple setOptions(Map<String, String> optionMap) {
        options.clear();
        for (Map.Entry<String, String> entry : optionMap.entrySet()) {
            options.add(new DropdownOption(entry.getKey(), entry.getValue()));
        }
        return this;
    }
    
    public AutreDropdownMultiple setSelectedValues(Set<String> values) {
        this.selectedValues.clear();
        this.selectedValues.addAll(values);
        return this;
    }
    
    public Set<String> getSelectedValues() {
        return new HashSet<>(selectedValues);
    }
    
    public AutreDropdownMultiple setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }
    
    public AutreDropdownMultiple setOnSelectionChange(Consumer<Set<String>> callback) {
        this.onSelectionChange = callback;
        return this;
    }
    
    private void handleClick(MouseClickEvent event) {
        if (!enabled || !visible || !event.pressed) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Check if click is on main dropdown button
        if (event.x >= absX && event.x <= absX + width &&
            event.y >= absY && event.y <= absY + height) {
            isOpen = !isOpen;
            return;
        }
        
        // Check if click is on dropdown items (when open)
        if (isOpen && event.x >= absX && event.x <= absX + width) {
            float dropdownY = absY + height + 2f;
            float actualDropdownHeight = Math.min(maxDropdownHeight, options.size() * itemHeight);
            
            if (event.y >= dropdownY && event.y <= dropdownY + actualDropdownHeight) {
                int itemIndex = (int) ((event.y - dropdownY) / itemHeight);
                if (itemIndex >= 0 && itemIndex < options.size()) {
                    DropdownOption option = options.get(itemIndex);
                    
                    // Toggle selection
                    if (selectedValues.contains(option.value)) {
                        selectedValues.remove(option.value);
                    } else {
                        selectedValues.add(option.value);
                    }
                    
                    if (onSelectionChange != null) {
                        onSelectionChange.accept(new HashSet<>(selectedValues));
                    }
                }
                return;
            }
        }
        
        // Click outside - close dropdown
        if (isOpen) {
            isOpen = false;
        }
    }
    
    private String getDisplayText() {
        if (selectedValues.isEmpty()) {
            return placeholder;
        } else if (selectedValues.size() == 1) {
            String value = selectedValues.iterator().next();
            return options.stream()
                .filter(opt -> opt.value.equals(value))
                .findFirst()
                .map(opt -> opt.label)
                .orElse(value);
        } else {
            return selectedValues.size() + " items selected";
        }
    }
    
    @Override
    protected void renderSelf(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        if (!visible) return;
        
        float absX = getAbsoluteX();
        float absY = getAbsoluteY();
        
        // Main dropdown button
        AutreRenderer2.fillRect(context.getMatrices(),
            absX, absY, width, height, backgroundColor);
        
        // Border
        AutreRenderer2.strokeRect(context.getMatrices(),
            absX, absY, width, height, 1f, borderColor);
        
        // Display text
        String displayText = getDisplayText();
        AutreRenderer2.Color currentTextColor = selectedValues.isEmpty() ? placeholderColor : textColor;
        
        float textX = absX + 6f;
        float textY = absY + (height - mc.textRenderer.fontHeight) / 2f;
        AutreRenderer2.drawText(context, displayText, textX, textY, currentTextColor, false);
        
        // Arrow indicator
        String arrow = isOpen ? "▲" : "▼";
        float arrowX = absX + width - 12f;
        AutreRenderer2.drawText(context, arrow, arrowX, textY, textColor, false);
        
        // Dropdown content (when open)
        if (isOpen && !options.isEmpty()) {
            float dropdownY = absY + height + 2f;
            float actualDropdownHeight = Math.min(maxDropdownHeight, options.size() * itemHeight);
            
            // Dropdown background
            AutreRenderer2.fillRect(context.getMatrices(),
                absX, dropdownY, width, actualDropdownHeight, dropdownBg);
            
            // Dropdown border
            AutreRenderer2.strokeRect(context.getMatrices(),
                absX, dropdownY, width, actualDropdownHeight, 1f, borderColor);
            
            // Dropdown items
            for (int i = 0; i < options.size(); i++) {
                float itemY = dropdownY + i * itemHeight;
                
                // Skip items that would be cut off
                if (itemY + itemHeight > dropdownY + actualDropdownHeight) {
                    break;
                }
                
                DropdownOption option = options.get(i);
                boolean isSelected = selectedValues.contains(option.value);
                boolean isHovered = mouseX >= absX && mouseX <= absX + width &&
                                  mouseY >= itemY && mouseY <= itemY + itemHeight;
                
                // Item background
                if (isSelected) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        absX, itemY, width, itemHeight, selectedColor);
                } else if (isHovered) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        absX, itemY, width, itemHeight, hoverColor);
                }
                
                // Checkbox
                float checkboxSize = 8f;
                float checkboxX = absX + 6f;
                float checkboxY = itemY + (itemHeight - checkboxSize) / 2f;
                
                // Checkbox background
                AutreRenderer2.fillRect(context.getMatrices(),
                    checkboxX, checkboxY, checkboxSize, checkboxSize, backgroundColor);
                
                // Checkbox border
                AutreRenderer2.strokeRect(context.getMatrices(),
                    checkboxX, checkboxY, checkboxSize, checkboxSize, 1f, borderColor);
                
                // Checkbox checkmark
                if (isSelected) {
                    AutreRenderer2.fillRect(context.getMatrices(),
                        checkboxX + 2f, checkboxY + 2f, checkboxSize - 4f, checkboxSize - 4f, checkboxColor);
                }
                
                // Item text
                float itemTextX = checkboxX + checkboxSize + 6f;
                float itemTextY = itemY + (itemHeight - mc.textRenderer.fontHeight) / 2f;
                AutreRenderer2.drawText(context, option.label, itemTextX, itemTextY, textColor, false);
            }
        }
    }
    
    // Helper class for dropdown options
    protected static class DropdownOption {
        public final String value;
        public final String label;
        
        public DropdownOption(String value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}