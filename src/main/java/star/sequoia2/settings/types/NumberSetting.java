package star.sequoia2.settings.types;

import star.sequoia2.settings.Setting;

public abstract class NumberSetting<T extends Number> extends Setting<T> {
    public final T min;
    public final T max;
    public final int scale;

    public NumberSetting(int ordinal, String name, String description,
                         T defaultValue, T value, T min, T max) {
        this(ordinal, name, description, defaultValue, value, min, max,
                (defaultValue instanceof Float || defaultValue instanceof Double) ? 2 : 0);
    }

    public NumberSetting(int ordinal, String name, String description,
                         T defaultValue, T value, T min, T max, int scale) {
        super(name, description, defaultValue, value, ordinal);
        this.min = min;
        this.max = max;
        if (defaultValue instanceof Float || defaultValue instanceof Double) {
            this.scale = scale > 0 ? scale : 2;
        } else {
            this.scale = 0;
        }
    }
}
