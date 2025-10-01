package star.sequoia2.autre.gui.demo;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import star.sequoia2.autre.gui.components.*;
import star.sequoia2.autre.gui.containers.*;
import star.sequoia2.autre.render.AutreRenderer2;

import java.awt.*;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Comprehensive demo showcasing ALL Autre components in a scrollable grid layout
 * This demo demonstrates every single component available in the Autre UI system
 */
public class AutreComprehensiveDemoGUI extends AutreContainer {
    
    // Grid layout configuration
    private static final int COLUMNS = 4;
    private static final float CELL_WIDTH = 220f;
    private static final float CELL_HEIGHT = 120f;
    private static final float PADDING = 15f;
    private static final float HEADER_HEIGHT = 40f;
    
    // Scrolling
    private float scrollOffset = 0f;
    private float maxScrollOffset = 0f;
    private final float scrollSpeed = 20f;
    
    public AutreComprehensiveDemoGUI() {
        super(0, 0, COLUMNS * (CELL_WIDTH + PADDING) + PADDING, 600f);
        initializeComponents();
        calculateScrollBounds();
    }
    
    private void initializeComponents() {
        // Add title header
        AutreLabel title = new AutreLabel(PADDING, PADDING, getWidth() - 2 * PADDING, 30f, 
            "🎨 COMPREHENSIVE AUTRE COMPONENT SHOWCASE 🎨");
        title.setTextColor(AutreRenderer2.Color.ACCENT)
             .setCentered(true);
        addChild(title);
        
        // Create demo components in grid layout
        int componentIndex = 0;
        
        // Row 1: Basic Components
        addChild(createButtonDemo(componentIndex++));
        addChild(createLabelDemo(componentIndex++));
        addChild(createTextInputDemo(componentIndex++));
        addChild(createCheckboxDemo(componentIndex++));
        
        // Row 2: Input Components
        addChild(createToggleButtonDemo(componentIndex++));
        addChild(createSliderDemo(componentIndex++));
        addChild(createRangeSliderDemo(componentIndex++));
        addChild(createSteppedSliderDemo(componentIndex++));
        
        // Row 3: Selection Components
        addChild(createDropdownDemo(componentIndex++));
        addChild(createDropdownMultipleDemo(componentIndex++));
        addChild(createRadioGroupDemo(componentIndex++));
        addChild(createListDemo(componentIndex++));
        
        // Row 4: Display Components
        addChild(createProgressBarDemo(componentIndex++));
        addChild(createPanelDemo(componentIndex++));
        addChild(createKnobDemo(componentIndex++));
        addChild(createColorPickerDemo(componentIndex++));
        
        // Row 5: Picker Components
        addChild(createDatePickerDemo(componentIndex++));
        addChild(createFilePickerDemo(componentIndex++));
        addChild(createBreadcrumbDemo(componentIndex++));
        addChild(createPaginatorDemo(componentIndex++));
        
        // Row 6: Menu Components
        addChild(createTopMenuDemo(componentIndex++));
        addChild(createNavigationMenuDemo(componentIndex++));
        addChild(createTabbedHeaderDemo(componentIndex++));
        addChild(createTopMenuBarDemo(componentIndex++));
        
        // Row 7: Container Components
        addChild(createCardDemo(componentIndex++));
        addChild(createAccordionMenuDemo(componentIndex++));
        addChild(createSideMenuDemo(componentIndex++));
        addChild(createSidebarDemo(componentIndex++));
        
        // Row 8: Overlay Components
        addChild(createModalDemo(componentIndex++));
        addChild(createToastDemo(componentIndex++));
        addChild(createTooltipMenuDemo(componentIndex++));
        addChild(createTextInputSmallDemo(componentIndex++));
    }
    
    private AutreContainer createButtonDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Button", "Interactive click button");
        
        AutreButton button = new AutreButton(5, 25, 100, 30, "Click Me! 🔥");
        button.setBackgroundColor(AutreRenderer2.Color.ACCENT);
        button.onClickStart(event -> {
            System.out.println("Demo button clicked!");
        });
        container.addChild(button);
        
        AutreLabel status = new AutreLabel(5, 60, "Status: Ready");
        status.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(status);
        
        return container;
    }
    
    private AutreContainer createLabelDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Label", "Text display component");
        
        AutreLabel label1 = new AutreLabel(5, 25, "Primary Text 📝");
        label1.setTextColor(AutreRenderer2.Color.TEXT_PRIMARY);
        container.addChild(label1);
        
        AutreLabel label2 = new AutreLabel(5, 45, "Secondary Text");
        label2.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(label2);
        
        AutreLabel label3 = new AutreLabel(5, 65, "Accent Text ⭐");
        label3.setTextColor(AutreRenderer2.Color.ACCENT);
        container.addChild(label3);
        
        return container;
    }
    
    private AutreContainer createTextInputDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Text Input", "Single line text entry");
        
        AutreTextInput textInput = new AutreTextInput(5, 25, 180, 25);
        textInput.setText("Type here... ⌨️");
        container.addChild(textInput);
        
        AutreLabel hint = new AutreLabel(5, 55, "Enter any text above");
        hint.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(hint);
        
        return container;
    }
    
    private AutreContainer createCheckboxDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Checkbox", "Boolean selection component");
        
        AutreCheckbox checkbox1 = new AutreCheckbox(5, 25, 150, 18, "Enable Feature A ✅");
        checkbox1.setChecked(true);
        container.addChild(checkbox1);
        
        AutreCheckbox checkbox2 = new AutreCheckbox(5, 48, 150, 18, "Enable Feature B");
        container.addChild(checkbox2);
        
        AutreCheckbox checkbox3 = new AutreCheckbox(5, 71, 150, 18, "Disabled Option");
        checkbox3.setEnabled(false);
        container.addChild(checkbox3);
        
        return container;
    }
    
    private AutreContainer createToggleButtonDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Toggle Button", "On/off switch button");
        
        AutreToggleButton toggle = new AutreToggleButton(5, 25, 80, 30, "Toggle");
        toggle.setToggled(true);
        container.addChild(toggle);
        
        AutreLabel status = new AutreLabel(5, 60, "State: ON");
        status.setTextColor(AutreRenderer2.Color.ACCENT_GREEN);
        container.addChild(status);
        
        return container;
    }
    
    private AutreContainer createSliderDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Slider", "Value selection slider");
        
        AutreSlider slider = new AutreSlider(5, 25, 150, 20, 0f, 100f, 75f);
        container.addChild(slider);
        
        AutreLabel value = new AutreLabel(5, 50, "Value: 75% 📊");
        value.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(value);
        
        return container;
    }
    
    private AutreContainer createRangeSliderDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Range Slider", "Dual-value range selection");
        
        AutreRangeSlider rangeSlider = new AutreRangeSlider(5, 25, 150, 20, 0f, 100f, 20f, 80f);
        container.addChild(rangeSlider);
        
        AutreLabel range = new AutreLabel(5, 50, "Range: 20-80 📏");
        range.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(range);
        
        return container;
    }
    
    private AutreContainer createSteppedSliderDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Stepped Slider", "Discrete value slider");
        
        AutreSteppedSlider steppedSlider = new AutreSteppedSlider(5f, 25f, 150f, 20f, 0f, 10f, 1f, 5f);
        container.addChild(steppedSlider);
        
        AutreLabel step = new AutreLabel(5, 50, "Step: 5 🎯");
        step.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(step);
        
        return container;
    }
    
    private AutreContainer createDropdownDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Dropdown", "Single selection dropdown");
        
        AutreDropdown dropdown = new AutreDropdown(5, 25, 150, 25);
        dropdown.addOption("Option 1 🎯")
                .addOption("Option 2 🎪")
                .addOption("Option 3 🎨");
        container.addChild(dropdown);
        
        AutreLabel selection = new AutreLabel(5, 55, "Select an option");
        selection.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(selection);
        
        return container;
    }
    
    private AutreContainer createDropdownMultipleDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Multi Dropdown", "Multiple selection dropdown");
        
        AutreDropdownMultiple multiDropdown = new AutreDropdownMultiple(5, 25, 150, 25);
        multiDropdown.addOption("Red 🔴", "red")
                    .addOption("Green 🟢", "green")
                    .addOption("Blue 🔵", "blue");
        container.addChild(multiDropdown);
        
        AutreLabel selections = new AutreLabel(5, 55, "Multi-select");
        selections.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(selections);
        
        return container;
    }
    
    private AutreContainer createRadioGroupDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Radio Group", "Exclusive selection group");
        
        // Create individual radio boxes (radio group functionality is built-in)
        AutreRadioBox radio1 = new AutreRadioBox(5, 25, "Option A 🅰️", "a");
        AutreRadioBox radio2 = new AutreRadioBox(5, 45, "Option B 🅱️", "b");
        AutreRadioBox radio3 = new AutreRadioBox(5, 65, "Option C ©️", "c");
        
        container.addChild(radio1);
        container.addChild(radio2);
        container.addChild(radio3);
        
        return container;
    }
    
    private AutreContainer createListDemo(int index) {
        AutreContainer container = createDemoContainer(index, "List", "Scrollable item list");
        
        AutreList list = new AutreList(5, 25, 180, 70);
        list.addItem("Item 1", "Description 1", event -> System.out.println("Item 1 selected"))
            .addItem("Item 2", "Description 2", event -> System.out.println("Item 2 selected"))
            .addItem("Item 3", "Description 3", event -> System.out.println("Item 3 selected"));
        container.addChild(list);
        
        return container;
    }
    
    private AutreContainer createProgressBarDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Progress Bar", "Progress indicator");
        
        AutreProgressBar progressBar = new AutreProgressBar(5, 25, 150, 20);
        progressBar.setProgress(0.65f);
        container.addChild(progressBar);
        
        AutreLabel progress = new AutreLabel(5, 50, "Progress: 65% ⚡");
        progress.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(progress);
        
        return container;
    }
    
    private AutreContainer createPanelDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Panel", "Container panel");
        
        AutrePanel panel = new AutrePanel(5, 25, 180, 70);
        panel.setBackgroundColor(AutreRenderer2.Color.SURFACE);
        
        AutreLabel panelContent = new AutreLabel(10, 10, "Panel Content 📦");
        panelContent.setTextColor(AutreRenderer2.Color.TEXT_PRIMARY);
        panel.addChild(panelContent);
        
        AutreButton panelButton = new AutreButton(10, 35, 80, 25, "Panel Btn");
        panel.addChild(panelButton);
        
        container.addChild(panel);
        return container;
    }
    
    private AutreContainer createKnobDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Knob", "Rotary control knob");
        
        AutreKnob knob = new AutreKnob(75, 35, 50);
        container.addChild(knob);
        
        AutreLabel knobValue = new AutreLabel(5, 90, "Value: 50% 🎛️");
        knobValue.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(knobValue);
        
        return container;
    }
    
    private AutreContainer createColorPickerDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Color Picker", "Color selection tool");
        
        AutreColorPicker colorPicker = new AutreColorPicker(5, 25, 180, 70);
        container.addChild(colorPicker);
        
        return container;
    }
    
    private AutreContainer createDatePickerDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Date Picker", "Date selection calendar");
        
        AutreDatePicker datePicker = new AutreDatePicker(5, 25, 180, 70);
        container.addChild(datePicker);
        
        return container;
    }
    
    private AutreContainer createFilePickerDemo(int index) {
        AutreContainer container = createDemoContainer(index, "File Picker", "File selection dialog");
        
        AutreFilePicker filePicker = new AutreFilePicker(5, 25, 180, 70);
        container.addChild(filePicker);
        
        return container;
    }
    
    private AutreContainer createBreadcrumbDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Breadcrumb", "Navigation breadcrumb");
        
        AutreBreadcrumb breadcrumb = new AutreBreadcrumb(5, 25, 180, 25);
        container.addChild(breadcrumb);
        
        AutreLabel path = new AutreLabel(5, 55, "Home > UI > Demo 🍞");
        path.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(path);
        
        return container;
    }
    
    private AutreContainer createPaginatorDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Paginator", "Page navigation");
        
        AutrePaginator paginator = new AutrePaginator(5, 25, 180, 30);
        container.addChild(paginator);
        
        AutreLabel pageInfo = new AutreLabel(5, 60, "Page 1 of 5 📄");
        pageInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(pageInfo);
        
        return container;
    }
    
    private AutreContainer createTopMenuDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Top Menu", "Horizontal menu bar");
        
        AutreTopMenu topMenu = new AutreTopMenu(5, 25, 180, 30);
        topMenu.addItem("File", "file", event -> System.out.println("File clicked"))
               .addItem("Edit", "edit", event -> System.out.println("Edit clicked"));
        container.addChild(topMenu);
        
        AutreLabel menuInfo = new AutreLabel(5, 60, "Click menu items 📋");
        menuInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(menuInfo);
        
        return container;
    }
    
    private AutreContainer createNavigationMenuDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Navigation Menu", "Vertical navigation");
        
        AutreNavigationMenu navMenu = new AutreNavigationMenu(5, 25, 120, 70);
        navMenu.addItem("🏠", "Home", event -> System.out.println("Home selected"))
               .addItem("⚙️", "Settings", event -> System.out.println("Settings selected"))
               .addItem("📊", "Stats", event -> System.out.println("Stats selected"));
        container.addChild(navMenu);
        
        return container;
    }
    
    private AutreContainer createTabbedHeaderDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Tabbed Header", "Tab navigation header");
        
        AutreTabbedHeader tabbedHeader = new AutreTabbedHeader(5, 25, 180, 30, "Tabs");
        tabbedHeader.addTab("Tab 1", event -> System.out.println("Tab 1 selected"))
                   .addTab("Tab 2", event -> System.out.println("Tab 2 selected"));
        container.addChild(tabbedHeader);
        
        AutreLabel tabInfo = new AutreLabel(5, 60, "Switch between tabs 📑");
        tabInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(tabInfo);
        
        return container;
    }
    
    private AutreContainer createTopMenuBarDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Top Menu Bar", "Alternative menu display");
        
        AutreLabel info = new AutreLabel(5, 25, "Alternative menu style");
        info.setTextColor(AutreRenderer2.Color.TEXT_PRIMARY);
        container.addChild(info);
        
        AutreButton menuBtn = new AutreButton(5, 45, 80, 25, "Menu Item");
        container.addChild(menuBtn);
        
        return container;
    }
    
    private AutreContainer createCardDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Card", "Content card container");
        
        AutreCard card = new AutreCard(5, 25, 180, 70);
        
        AutreLabel cardContent = new AutreLabel(10, 25, "Card content here 🎴");
        cardContent.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        card.addChild(cardContent);
        
        container.addChild(card);
        return container;
    }
    
    private AutreContainer createAccordionMenuDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Accordion Menu", "Collapsible menu");
        
        AutreAccordionMenu accordion = new AutreAccordionMenu(5, 25, 180, 70);
        accordion.addSection("Section 1", "Content 1", true)
                .addSection("Section 2", "Content 2", false);
        container.addChild(accordion);
        
        return container;
    }
    
    private AutreContainer createSideMenuDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Side Menu", "Sidebar menu");
        
        AutreSideMenu sideMenu = new AutreSideMenu(5, 25, 100, 70);
        sideMenu.addMenuItem("Home", "🏠", "home")
               .addMenuItem("Settings", "⚙️", "settings");
        container.addChild(sideMenu);
        
        return container;
    }
    
    private AutreContainer createSidebarDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Sidebar", "Application sidebar");
        
        // Use enum for Side parameter
        AutreSidebar sidebar = new AutreSidebar(5, 25, 80, 70, AutreSidebar.Side.LEFT);
        container.addChild(sidebar);
        
        AutreLabel sidebarInfo = new AutreLabel(90, 45, "Sidebar ➡️");
        sidebarInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(sidebarInfo);
        
        return container;
    }
    
    private AutreContainer createModalDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Modal", "Overlay dialog");
        
        AutreButton modalButton = new AutreButton(5, 25, 100, 30, "Open Modal");
        modalButton.onClickStart(event -> {
            AutreModal modal = new AutreModal(100, 100, 300, 200);
            AutreLabel modalContent = new AutreLabel(20, 40, "This is a modal! 📢");
            modal.addChild(modalContent);
            System.out.println("Modal would open here!");
        });
        container.addChild(modalButton);
        
        AutreLabel modalInfo = new AutreLabel(5, 60, "Click to open 📢");
        modalInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(modalInfo);
        
        return container;
    }
    
    private AutreContainer createToastDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Toast", "Notification toast");
        
        AutreButton toastButton = new AutreButton(5, 25, 100, 30, "Show Toast");
        toastButton.onClickStart(event -> {
            // Toast would be displayed here
            new AutreToast(50f, 50f, 200f, 40f, "Demo notification! 🍞");
            System.out.println("Toast would show: Demo notification!");
        });
        container.addChild(toastButton);
        
        AutreLabel toastInfo = new AutreLabel(5, 60, "Click for toast 🍞");
        toastInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(toastInfo);
        
        return container;
    }
    
    private AutreContainer createTooltipMenuDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Tooltip Menu", "Context menu tooltip");
        
        AutreTooltipMenu tooltipMenu = new AutreTooltipMenu(5f, 25f);
        container.addChild(tooltipMenu);
        
        AutreLabel tooltipInfo = new AutreLabel(5, 95, "Hover for tooltip 💬");
        tooltipInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(tooltipInfo);
        
        return container;
    }
    
    private AutreContainer createTextInputSmallDemo(int index) {
        AutreContainer container = createDemoContainer(index, "Small Text Input", "Compact text input");
        
        AutreTextInputSmall smallInput = new AutreTextInputSmall(5, 25, 120, 20);
        smallInput.setText("Small text... ✏️");
        container.addChild(smallInput);
        
        AutreLabel smallInfo = new AutreLabel(5, 50, "Compact input field");
        smallInfo.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY);
        container.addChild(smallInfo);
        
        return container;
    }
    
    private AutreContainer createDemoContainer(int index, String title, String description) {
        int col = index % COLUMNS;
        int row = index / COLUMNS;
        
        float x = PADDING + col * (CELL_WIDTH + PADDING);
        float y = HEADER_HEIGHT + PADDING + row * (CELL_HEIGHT + PADDING);
        
        AutreContainer container = new AutreContainer(x, y, CELL_WIDTH, CELL_HEIGHT);
        container.setBackgroundColor(AutreRenderer2.Color.SURFACE);
        
        // Title
        AutreLabel titleLabel = new AutreLabel(5, 5, CELL_WIDTH - 10, 15, title);
        titleLabel.setTextColor(AutreRenderer2.Color.ACCENT)
                  .setCentered(false);
        container.addChild(titleLabel);
        
        // Description
        AutreLabel descLabel = new AutreLabel(5, 100, CELL_WIDTH - 10, 15, description);
        descLabel.setTextColor(AutreRenderer2.Color.TEXT_SECONDARY)
                 .setCentered(false);
        container.addChild(descLabel);
        
        return container;
    }
    
    private void calculateScrollBounds() {
        // Calculate total content height
        int totalComponents = 32; // Total number of demo components
        int rows = (totalComponents + COLUMNS - 1) / COLUMNS;
        float totalContentHeight = HEADER_HEIGHT + PADDING + rows * (CELL_HEIGHT + PADDING) + PADDING;
        
        maxScrollOffset = Math.max(0, totalContentHeight - getHeight());
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float deltaTime) {
        AutreRenderer2.pushScissorGUI(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());

        AutreRenderer2.fillRect(context.getMatrices(), 0, 0, getWidth(), getHeight(), AutreRenderer2.Color.BACKGROUND);
        AutreRenderer2.strokeRect(context.getMatrices(), 0, 0, getWidth(), getHeight(), 2f, AutreRenderer2.Color.ACCENT);

        float scrolledMouseY = mouseY + scrollOffset;

        context.getMatrices().push();
        context.getMatrices().translate(0, -scrollOffset, 0);

        for (AutreComponent child : getChildren()) {
            if (child.isVisible()) {
                float childY = child.getY() - scrollOffset;
                if (childY + child.getHeight() >= -50 && childY <= getHeight() + 50) {
                    child.render(context, mouseX, scrolledMouseY, deltaTime);
                }
            }
        }

        context.getMatrices().pop();

        AutreRenderer2.popScissor();

        if (maxScrollOffset > 0) {
            drawScrollIndicator(context);
        }
    }

    private void drawScrollIndicator(DrawContext context) {
        float scrollBarWidth = 10f;
        float scrollBarHeight = getHeight() - 20f;
        float scrollBarX = getWidth() + 200 - scrollBarWidth - 5f;
        float scrollBarY = 10f;
        
        // Draw scroll track with flat colors
        AutreRenderer2.fillRect(context.getMatrices(), scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight,
                               AutreRenderer2.Color.SURFACE);
        AutreRenderer2.strokeRect(context.getMatrices(), scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, 1f,
                                 AutreRenderer2.Color.TEXT_SECONDARY);
        
        // Draw scroll thumb
        float thumbHeight = Math.max(30f, scrollBarHeight * (getHeight() / (getHeight() + maxScrollOffset)));
        float thumbY = scrollBarY + (scrollBarHeight - thumbHeight) * (scrollOffset / maxScrollOffset);
        
        AutreRenderer2.fillRect(context.getMatrices(), scrollBarX + 1f, thumbY, scrollBarWidth - 2f, thumbHeight,
                               AutreRenderer2.Color.ACCENT);
    }
    
    @Override
    public boolean handleMouseClick(float mouseX, float mouseY, int button, boolean pressed) {
        // Check if click is within our bounds first
        if (mouseX < 0 || mouseX > getWidth() || mouseY < 0 || mouseY > getHeight()) {
            return false;
        }
        
        // Adjust mouse coordinates for scrolling
        float adjustedY = mouseY + scrollOffset;
        
        // Forward to children with adjusted coordinates
        for (int i = getChildren().size() - 1; i >= 0; i--) {
            AutreComponent child = getChildren().get(i);
            if (child.isVisible()) {
                float relativeX = mouseX - child.getX();
                float relativeY = adjustedY - child.getY();
                
                if (relativeX >= 0 && relativeX <= child.getWidth() &&
                    relativeY >= 0 && relativeY <= child.getHeight()) {
                    if (child.handleMouseClick(relativeX, relativeY, button, pressed)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    // Mouse wheel scrolling
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (maxScrollOffset > 0) {
            float oldScrollOffset = scrollOffset;
            scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset - (float)scrollAmount * scrollSpeed));
            return scrollOffset != oldScrollOffset;
        }
        return false;
    }
}