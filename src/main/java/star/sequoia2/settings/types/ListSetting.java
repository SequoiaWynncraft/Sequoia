package star.sequoia2.settings.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import star.sequoia2.configuration.JsonCompound;
import star.sequoia2.settings.CommandSupport;
import star.sequoia2.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListSetting<T> extends Setting<List<T>> implements CommandSupport {

    private final List<?> allowedGroups;

    private boolean whitelist = true;

    public ListSetting(int ordinal, String name, String description, List<T> defaultValue, List<T> value, List<?> allowedGroups) {
        super(name, description, defaultValue, value, ordinal);
        this.allowedGroups = allowedGroups;
    }

    public ListSetting(int ordinal, String name, String description, List<T> defaultValue, List<T> value) {
        this(ordinal, name, description, defaultValue, value, new ArrayList<>());
    }

    public List<?> getAllowedGroups() {
        return allowedGroups;
    }

    public boolean getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    @SuppressWarnings("unchecked")
    public List<T> getListedElements() {
        List<?> saved = get();
        List<T> result = new ArrayList<>();

        List<Registry<?>> registries = List.of(Registries.BLOCK, Registries.ITEM, Registries.ENTITY_TYPE);
        if (!allowedGroups.isEmpty() && allowedGroups.get(0) instanceof Registry) {
            registries = allowedGroups.stream()
                    .filter(item -> item instanceof Registry)
                    .map(item -> (Registry<?>) item)
                    .collect(Collectors.toList());
        }

        for (Object savedObj : saved) {
            String search = savedObj.toString().toLowerCase().trim();
            T candidate = null;
            for (Registry<?> registry : registries) {
                if (registry == Registries.BLOCK) {
                    for (Block block : Registries.BLOCK) {
                        String blockName = block.getTranslationKey().replace("block.minecraft.", "").toLowerCase();
                        if (blockName.equals(search)) {
                            candidate = (T) block;
                            break;
                        }
                    }
                } else if (registry == Registries.ITEM) {
                    for (Item item : Registries.ITEM) {
                        if (item instanceof BlockItem) {
                            String itemName = item.getTranslationKey().replace("item.minecraft.", "").toLowerCase();
                            itemName = itemName.replace("block.minecraft.", "");
                            if (itemName.equals(search)) {
                                candidate = (T) item;
                                break;
                            }
                        }
                        String itemName = item.getTranslationKey().replace("item.minecraft.", "").toLowerCase();
                        if (itemName.equals(search)) {
                            candidate = (T) item;
                            break;
                        }
                    }
                } else if (registry == Registries.ENTITY_TYPE) {
                    for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                        String entityName = entityType.getTranslationKey().replace("entity.minecraft.", "").toLowerCase();
                        if (entityName.equals(search)) {
                            candidate = (T) entityType;
                            break;
                        }
                    }
                }
                if (candidate != null) {
                    break;
                }
            }
            if (candidate != null) {
                result.add(candidate);
            }
        }
        return result;
    }



    @Override
    public void load(JsonCompound json) {
        JsonArray jsonArray = json.getList("value");
        List<T> list = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            list.add((T) element.getAsString());
        }
        setInternal(list);

        if (json.contains("whitelist")) {
            this.whitelist = json.getBoolean("whitelist");
        }
    }

    @Override
    protected JsonCompound toJson(JsonCompound json) {
        JsonArray array = new JsonArray();
        for (T element : get()) {
            array.add(new JsonPrimitive(element.toString()));
        }
        json.put("value", array);
        json.putBoolean("whitelist", this.whitelist);
        return json;
    }

    @Override
    public String toPrintableValue() {
        return get().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public void parseValueFromCommand(String value) {
        if (value == null || value.trim().isEmpty()) {
            set(new ArrayList<>());
            return;
        }
        String[] parts = value.split(",");
        List<T> list = new ArrayList<>();
        for (String part : parts) {
            list.add((T) part.trim());
        }
        set(list);
    }
}
