package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.CommandSupport;
import star.sequoia2.settings.Setting;
import net.minecraft.text.Text;

/**
 * Text setting
 */
public class TextSetting extends Setting<String> implements CommandSupport {

    public TextSetting(int ordinal, String name, String description, String defaultValue, String value) {
        super(name, description, defaultValue, value, ordinal);
    }

    @Override
    public void load(JsonCompound json) {
        setInternal(json.getString("value"));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putString("value", get());
        return json;
    }

    public Text getValueText() {
        return Text.of(get());
    }

    @Override
    public String toPrintableValue() {
        return get();
    }

    @Override
    public void parseValueFromCommand(String value) {
        set(value);
    }
}
