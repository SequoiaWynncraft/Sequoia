package star.sequoia2.settings.types;


import star.sequoia2.configuration.JsonCompound;

public class FloatSetting extends NumberSetting<Float> {

    public FloatSetting(int ordinal, String name, String description, Float defaultValue, Float value, Float min, Float max) {
        super(ordinal, name, description, defaultValue, value, min, max);
    }

    public FloatSetting(int ordinal, String name, String description, Float defaultValue, Float value, Float min, Float max, int scale) {
        super(ordinal, name, description, defaultValue, value, min, max, scale);
    }

    @Override
    public void load(JsonCompound compound) {
        setInternal(compound.getFloat("value"));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putDouble("value", get());
        return json;
    }

    @Override
    public String toPrintableValue() {
        return String.format("%." + scale + "f", get());
    }

    @Override
    public void parseValueFromCommand(String value) {
        set(Float.parseFloat(value));
    }
}
