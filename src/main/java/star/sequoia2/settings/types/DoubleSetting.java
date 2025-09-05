package star.sequoia2.settings.types;


import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.CommandSupport;

public class DoubleSetting extends NumberSetting<Double> implements CommandSupport {

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

    @Override
    public String toPrintableValue() {
        return String.format("%." + scale + "f", get());
    }

    @Override
    public void parseValueFromCommand(String value) {
        set(Double.parseDouble(value));
    }
}
