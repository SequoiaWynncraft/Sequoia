package star.sequoia2.settings;

import com.google.common.base.MoreObjects;
import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.events.SettingChanged;
import lombok.Getter;

public abstract class Setting<T> implements EventBusAccessor, Named {
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public final String name;
    public final String description;
    protected final T defaultValue;
    private T value;
    @Getter
    private final int ordinal;

    public Setting(String name, String description, T defaultValue, T value, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    public T get() {
        return MoreObjects.firstNonNull(value, defaultValue);
    }

    public T set(T value) {
        if (!value.equals(defaultValue) || !this.value.equals(value)) {
            this.value = value;
            onChange();
        }
        return this.value;
    }

    protected void setInternal(T value) {
        this.value = value;
    }

    public T reset() {
        if (!this.defaultValue.equals(this.value)) {
            this.value = this.defaultValue;
            onChange();
        }
        return this.value;
    }

    private void onChange() {
        dispatch(new SettingChanged(this));
    }

    public boolean isDefaultValue() {
        return get().equals(defaultValue);
    }

    public abstract void load(JsonCompound json);

    public final JsonCompound toJson() {
        JsonCompound json = new JsonCompound();
        if (shouldSaveDefaultValues() || !isDefaultValue()) {
            json.putString(TYPE, this.getClass().getName());
            json.putString(NAME, this.name);
            json = toJson(json);
        }
        return json;
    }

    protected abstract JsonCompound toJson(JsonCompound json);

    protected boolean shouldSaveDefaultValues() {
        return false;
    }
}
