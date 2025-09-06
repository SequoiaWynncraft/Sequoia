package star.sequoia2.utils.render;

import net.minecraft.text.Style;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.features.impl.Settings;

import java.util.function.UnaryOperator;

public class Theme implements FeaturesAccessor {
    String NAME;
    public int LIGHT;
    public int NORMAL;
    public int DARK;
    public int ACCENT1;
    public int ACCENT2;
    public int ACCENT3;
    public int ERROR;

    public Theme(String name, int light, int normal, int dark, int accent1, int accent2, int accent3) {
        this.NAME = name;
        this.LIGHT = light;
        this.NORMAL = normal;
        this.DARK = dark;
        this.ACCENT1 = accent1;
        this.ACCENT2 = accent2;
        this.ACCENT3 = accent3;
        this.ERROR = 0xFF5555;
    }

    public UnaryOperator<Style> light()   { return s -> s.withColor(features().get(Settings.class).map(settingsFeature -> settingsFeature.getTheme().get().getTheme().LIGHT).orElse(0xf3e6ff));   }
    public UnaryOperator<Style> normal()  { return s -> s.withColor(features().get(Settings.class).map(settingsFeature -> settingsFeature.getTheme().get().getTheme().NORMAL).orElse(0xa64dff));  }
    public UnaryOperator<Style> dark()    { return s -> s.withColor(features().get(Settings.class).map(settingsFeature -> settingsFeature.getTheme().get().getTheme().DARK).orElse(0x6600cc));    }
    public UnaryOperator<Style> accent1() { return s -> s.withColor(features().get(Settings.class).map(settingsFeature -> settingsFeature.getTheme().get().getTheme().ACCENT1).orElse(0xffc34d)); }
    public UnaryOperator<Style> accent2() { return s -> s.withColor(features().get(Settings.class).map(settingsFeature -> settingsFeature.getTheme().get().getTheme().ACCENT2).orElse(0x66e0ff)); }
    public UnaryOperator<Style> accent3() { return s -> s.withColor(features().get(Settings.class).map(settingsFeature -> settingsFeature.getTheme().get().getTheme().ACCENT3).orElse(0xff3399)); }

    public Theme() {}

    public String getName() { return this.NAME; }
}