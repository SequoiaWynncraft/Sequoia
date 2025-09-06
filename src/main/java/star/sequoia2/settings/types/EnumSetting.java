package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Setting;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {

    private final Class<T> enumClazz;

    public EnumSetting(int ordinal, String name, String description, T defaultValue, T value, Class<T> enumClazz) {
        super(name, description, defaultValue, value, ordinal);
        this.enumClazz = enumClazz;
    }

    public String getValueName() {
        return get().name();
    }

    @Override
    public void load(JsonCompound json) {
        T value;
        try {
            value = Enum.valueOf(enumClazz, json.getString("value"));
        } catch (IllegalArgumentException e) {
            value = defaultValue;
        }
        setInternal(value);
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putString("value", get().name());
        return json;
    }
}
