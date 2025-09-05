package star.sequoia2.settings.types;

import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CalculatedEnumSetting<O extends Option> extends Setting<String> {

    private final Supplier<Set<O>> supplier;

    public CalculatedEnumSetting(int ordinal, String name, String description, String defaultValue, String value, Supplier<Set<O>> supplier) {
        super(name, description, defaultValue, value, ordinal);
        this.supplier = supplier;
    }

    public List<String> getOptions() {
        return supplier.get().stream().map(Option::name).collect(Collectors.toCollection(LinkedList::new));
    }

    public O getOption() {
        return supplier.get().stream().filter(font -> get().equals(font.name())).findFirst().orElseThrow();
    }

    @Override
    public void load(JsonCompound json) {
        String name = json.getString("value");
        supplier.get().stream().filter(t -> t.name().equals(name)).findFirst().ifPresentOrElse(o -> {
            setInternal(o.name());
        }, () -> {
            setInternal(defaultValue);
        });
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        json.putString("value", get());
        return json;
    }
}
