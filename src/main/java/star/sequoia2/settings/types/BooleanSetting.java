package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Setting;

/**
 * Boolean setting
 */
public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(int ordinal, String name, String description, Boolean defaultValue, Boolean value) {
        super(name, description, defaultValue, value, ordinal);
    }

    @Override
    public void load(JsonCompound json) {
        setInternal(json.getBoolean("value"));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putBoolean("value", get());
        return json;
    }
}
