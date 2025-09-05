package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.CommandSupport;

/**
 * Integer setting
 */
public class IntSetting extends NumberSetting<Integer> implements CommandSupport {

    public IntSetting(int ordinal, String name, String description, Integer defaultValue, Integer value, Integer min, Integer max) {
        super(ordinal, name, description, defaultValue, value, min, max);
    }

    @Override
    public void load(JsonCompound compound) {
        setInternal(compound.getInt("value"));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putInt("value", get());
        return json;
    }

    @Override
    public String toPrintableValue() {
        return get().toString();
    }

    @Override
    public void parseValueFromCommand(String value) {
        set(Integer.parseInt(value));
    }
}
