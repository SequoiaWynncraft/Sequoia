package star.sequoia2.settings;

import mil.nga.color.Color;
import star.sequoia2.settings.types.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Settings {
    private final ConcurrentMap<String, Setting<?>> settings = new ConcurrentHashMap<>();
    private final AtomicInteger ordinalCounter = new AtomicInteger(0);

    public TextSetting text(String name, String description, String defaultValue) {
        return (TextSetting) add(name, new TextSetting( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue));
    }

    public IntSetting number(String name, String description, int defaultValue, int min, int max) {
        return (IntSetting) add(name, new IntSetting(ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue, min, max));
    }

    public DoubleSetting number(String name, String description, double defaultValue, double min, double max) {
        return (DoubleSetting) add(name, new DoubleSetting( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue, min, max));
    }

    public FloatSetting number(String name, String description, float defaultValue, float min, float max) {
        return (FloatSetting) add(name, new FloatSetting( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue, min, max));
    }

    public BooleanSetting bool(String name, String description, boolean defaultValue) {
        return (BooleanSetting) add(name, new BooleanSetting( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue));
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> EnumSetting<T> options(String name, String description, T defaultValue, Class<T> clazz) {
        return (EnumSetting<T>) add(name, new EnumSetting<>( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue, clazz));
    }

    @SuppressWarnings("unchecked")
    public <O extends Option> CalculatedEnumSetting<O> options(String name, String description, String defaultValue, Supplier<Set<O>> supplier) {
        return (CalculatedEnumSetting<O>) add(name, new CalculatedEnumSetting<>( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue, supplier));
    }

    public BooleanListSetting booleanList(String name, String description, HashMap<String, Boolean> defaultValue) {
        return (BooleanListSetting) add(name, new BooleanListSetting(ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue));
    }

    public ColorSetting color(String name, String description, Color defaultValue) {
        return (ColorSetting) add(name, new ColorSetting( ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue));
    }

    public KeybindSetting binding(String name, String description, Binding defaultValue) {
        return (KeybindSetting) add(name, new KeybindSetting(ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue));
    }

    @SuppressWarnings("unchecked")
    public <T> ListSetting<T> list(String name, String description, List<T> defaultValue, List<?> allowedGroups) {
        return (ListSetting<T>) add(name, new ListSetting<>(ordinalCounter.getAndIncrement(), name, description, defaultValue, defaultValue, allowedGroups));
    }

    public Stream<Setting<?>> all() {
        return settings.values().stream().sorted(Comparator.comparingInt(Setting::getOrdinal));
    }

    private Setting<?> add(String name, Setting<?> setting) {
        return settings.compute(name, (s, theSetting) -> {
            if (theSetting == null) {
                theSetting = setting;
            }
            return theSetting;
        });
    }
}
