package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Setting;
import mil.nga.color.Color;

public class ColorSetting extends Setting<Color> {

    private boolean isGlobal;

    public ColorSetting(int ordinal, String name, String description, Color defaultValue, Color value, boolean isGlobal) {
        super(name, description, defaultValue, value, ordinal);
        this.isGlobal = isGlobal;
    }

    public ColorSetting(int ordinal, String name, String description, Color defaultValue, Color value) {
        super(name, description, defaultValue, value, ordinal);
        this.isGlobal = false;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    @Override
    public void load(JsonCompound json) {
        json.putBoolean("isGlobal", isGlobal);
        setInternal(new Color(json.getInt("value")));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putInt("value", this.get().getColorWithAlpha());
        json.putBoolean("isGlobal", isGlobal);
        return json;
    }
}
