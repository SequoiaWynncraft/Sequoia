package star.sequoia2.settings.types;


import star.sequoia2.configuration.JsonCompound;

public class DoubleSetting extends NumberSetting<Double> {

    public DoubleSetting(int ordinal, String name, String description, Double defaultValue, Double value, Double min, Double max) {
        super(ordinal, name, description, defaultValue, value, min, max);
    }

    public DoubleSetting(int ordinal, String name, String description, Double defaultValue, Double value, Double min, Double max, int scale) {
        super(ordinal, name, description, defaultValue, value, min, max, scale);
    }

    @Override
    public void load(JsonCompound compound) {
        setInternal(compound.getDouble("value"));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putDouble("value", get());
        return json;
    }
}
