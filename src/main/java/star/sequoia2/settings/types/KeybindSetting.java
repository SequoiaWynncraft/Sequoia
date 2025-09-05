package star.sequoia2.settings.types;


import lombok.Setter;
import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Binding;
import star.sequoia2.settings.Setting;

@Setter
public class KeybindSetting extends Setting<Binding> {
    private boolean toggle;

    public KeybindSetting(int ordinal, String name, String description, Binding defaultValue, Binding value) {
        super(name, description, defaultValue, value, ordinal);
        this.toggle = true;
    }

    public boolean getToggle() {
        return toggle;
    }

    @Override
    public void load(JsonCompound compound) {
        this.toggle = compound.getBoolean("TOGGLE");
        setInternal(Binding.from(compound));
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        get().toJSON(json);
        json.putBoolean("TOGGLE", toggle);
        return json;
    }
}
