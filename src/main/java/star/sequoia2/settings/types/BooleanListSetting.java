package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Setting;

import java.util.HashMap;
import java.util.Map;

public class BooleanListSetting extends Setting<HashMap<String, Boolean>> {

    public BooleanListSetting(int ordinal, String name, String description, HashMap<String, Boolean> defaultValue, HashMap<String, Boolean> value) {
        super(name, description, defaultValue, value, ordinal);
    }

    @Override
    public void load(JsonCompound json) {
        HashMap<String, Boolean> currentValues = get();
        for (String key : currentValues.keySet()) {
            if (json.contains(key)) {
                currentValues.put(key, json.getBoolean(key));
            }
        }
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        for (Map.Entry<String, Boolean> entry : this.get().entrySet()) {
            String string = entry.getKey();
            Boolean aBoolean = entry.getValue();
            json.putBoolean(string, aBoolean);
        }
        return json;
    }
}