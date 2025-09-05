package star.sequoia2.features;

import com.google.gson.JsonArray;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.SettingsAccessor;
import star.sequoia2.configuration.JSONConfiguration;
import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Setting;
import star.sequoia2.settings.Settings;
import lombok.Getter;


public abstract class Feature implements JSONConfiguration, SettingsAccessor, FeaturesAccessor {

    public static final String FEATURE = "feature";
    public static final String CLASS = "class";

    @Getter
    public final String name;
    public final String description;

    public Feature(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String commandName() {
        return this.name.replace(" ", "").toLowerCase();
    }

    protected Settings settings() {
        return settingsState().fromFeature(this);
    }

    public void reset() {
        settings().all().forEach(Setting::reset);
    }

    @Override
    public JsonCompound toJSON() {
        JsonCompound json = new JsonCompound();
        JsonArray jsonArray = new JsonArray();
        this.settings().all().forEach(setting -> {
            JsonCompound featureSetting = setting.toJson();
            if (!featureSetting.isEmpty()) {
                jsonArray.add(featureSetting);
            }
        });
        json.put(FEATURE, jsonArray);
        json.putString(CLASS, this.getClass().getName());
        return json;
    }

    @Override
    public void fromJSON(JsonCompound compound) {
        this.settings().all().forEach(setting -> {
            compound.getList(FEATURE).forEach(jsonElement -> {
                JsonCompound settingJson = JsonCompound.wrap(jsonElement);
                String settingName = settingJson.getString(Setting.NAME);
                if (setting.name.equals(settingName)) {
                    setting.load(settingJson);
                }
            });
        });
    }
}
