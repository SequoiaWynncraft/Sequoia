package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.autre.gui.demo.AutreComprehensiveDemoScreen;
import star.sequoia2.events.input.KeyEvent;
import star.sequoia2.features.Feature;
import star.sequoia2.settings.Binding;
import star.sequoia2.settings.types.KeybindSetting;

import static star.sequoia2.client.SeqClient.mc;

/**
 * Feature that provides access to the comprehensive Autre UI component demo
 * Press H to open a scrollable showcase of ALL available Autre components
 */
public class AutreGui2Feature extends Feature {

    public final KeybindSetting guiKey = settings().binding("GUI Key", "Opens the comprehensive Autre component demo", Binding.withKey(GLFW.GLFW_KEY_H)); // H key

    public AutreGui2Feature() {
        super("AutreGUI2", "🎨 Comprehensive Autre UI Component Showcase - Press H to open demo with ALL components!");
    }

    @Subscribe
    public void onKeyDown(KeyEvent event) {
        if (event.isKeyDown() && this.guiKey.get().matches(event) && mc.currentScreen == null) {
            // Open the comprehensive demo screen
            mc.setScreen(new AutreComprehensiveDemoScreen());
            System.out.println("🎨 Comprehensive Autre Component Demo opened! Press ESC to close.");
            System.out.println("📋 This demo showcases ALL available Autre UI components in a scrollable grid layout:");
            System.out.println("   • Basic Components: Button, Label, TextInput, Checkbox");
            System.out.println("   • Input Components: Toggle, Slider, Range Slider, Stepped Slider");
            System.out.println("   • Selection: Dropdown, Multi-Dropdown, Radio Group, List");
            System.out.println("   • Display: Progress Bar, Panel, Knob, Color Picker");
            System.out.println("   • Pickers: Date Picker, File Picker, Breadcrumb, Paginator");
            System.out.println("   • Menus: Top Menu, Navigation Menu, Tabbed Header");
            System.out.println("   • Containers: Card, Accordion, Side Menu, Sidebar");
            System.out.println("   • Overlays: Modal, Toast, Tooltip Menu, Small Text Input");
            System.out.println("💡 Scroll with mouse wheel to see all components!");
            event.cancel();
        }
    }
}