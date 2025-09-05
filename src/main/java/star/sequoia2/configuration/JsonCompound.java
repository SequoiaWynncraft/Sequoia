package star.sequoia2.configuration;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JsonCompound extends JsonElement {
    private final LinkedTreeMap<String, JsonElement> members;

    public JsonCompound() {
        this(new LinkedTreeMap<>(false));
    }

    @SuppressWarnings("deprecation") // superclass constructor
    private JsonCompound(Map<String, JsonElement> members) {
        this.members = (LinkedTreeMap<String, JsonElement>) members;
    }

    public static JsonCompound wrap(JsonElement element) {
        if (element instanceof JsonCompound compound) {
            return compound;
        } else if (element instanceof JsonObject object) {
            return new JsonCompound(object.asMap());
        } else {
            throw new ClassCastException("Object cannot be cast to JsonCompound");
        }
    }

    @Override
    public JsonCompound deepCopy() {
        JsonCompound result = new JsonCompound();
        for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
            result.put(entry.getKey(), entry.getValue().deepCopy());
        }
        return result;
    }

    public boolean contains(String key) {
        return members.containsKey(key);
    }

    @Nullable
    public JsonElement put(String property, JsonElement value) {
        return members.put(property, value == null ? JsonNull.INSTANCE : value);
    }

    public void putByte(String key, byte value) {
        put(key, new JsonPrimitive(value));
    }

    public void putShort(String key, short value) {
        put(key, new JsonPrimitive(value));
    }

    public void putInt(String key, int value) {
        put(key, new JsonPrimitive(value));
    }

    public void putLong(String key, long value) {
        put(key, new JsonPrimitive(value));
    }

    public void putFloat(String key, float value) {
        put(key, new JsonPrimitive(value));
    }

    public void putDouble(String key, double value) {
        put(key, new JsonPrimitive(value));
    }

    public void putString(String key, String value) {
        put(key, new JsonPrimitive(value));
    }

    public void putBoolean(String key, boolean value) {
        put(key, new JsonPrimitive(value));
    }

    public void putUuid(String key, UUID value) {
        put(key, new JsonPrimitive(value.toString()));
    }

    public byte getByte(String key) {
        try {
            if (contains(key)) {
                return members.get(key).getAsByte();
            }
        } catch (UnsupportedOperationException ignored) {}

        return 0;
    }

    public short getShort(String key) {
        try {
            if (contains(key)) {
                return members.get(key).getAsShort();
            }
        } catch (UnsupportedOperationException ignored) {}

        return 0;
    }

    public int getInt(String key) {
        try {
            if (contains(key)) {
                return members.get(key).getAsInt();
            }
        } catch (UnsupportedOperationException ignored) {}

        return 0;
    }

    public long getLong(String key) {
        try {
            if (contains(key)) {
                return members.get(key).getAsLong();
            }
        } catch (UnsupportedOperationException ignored) {}

        return 0L;
    }

    public float getFloat(String key) {
        try {
            if (this.contains(key)) {
                return members.get(key).getAsFloat();
            }
        } catch (UnsupportedOperationException ignored) {}

        return 0.0F;
    }

    public double getDouble(String key) {
        try {
            if (this.contains(key)) {
                return members.get(key).getAsDouble();
            }
        } catch (UnsupportedOperationException ignored) {}

        return 0.0;
    }

    public String getString(String key) {
        try {
            if (this.contains(key)) {
                return members.get(key).getAsString();
            }
        } catch (UnsupportedOperationException ignored) {}

        return "";
    }

    public boolean getBoolean(String key) {
        try {
            if (this.contains(key)) {
                return members.get(key).getAsBoolean();
            }
        } catch (UnsupportedOperationException ignored) {}

        return false;
    }

    public UUID getUuid(String key) {
        try {
            if (this.contains(key)) {
                return UUID.fromString(members.get(key).getAsString());
            }
        } catch (UnsupportedOperationException | IllegalArgumentException ignored) {}

        return new UUID(0L, 0L);
    }

    public JsonCompound getCompound(String key) {
        if (contains(key)) {
            if (members.get(key) instanceof JsonCompound compound) {
                return compound;
            } else if (members.get(key) instanceof JsonObject object) {
                return JsonCompound.wrap(object);
            }
        }

        return new JsonCompound();
    }

    public JsonArray getList(String key) {
        if (contains(key)) {
            return members.get(key).getAsJsonArray();
        }

        return new JsonArray();
    }

    public int size() {
        return members.size();
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public Set<Map.Entry<String, JsonElement>> entrySet() {
        return members.entrySet();
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    public Map<String, JsonElement> asMap() {
        // It is safe to expose the underlying map because it disallows null keys and values
        return members;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonObject getAsJsonObject() {
        // I will not apologise for this
        JsonObject jsonObject = new JsonObject();
        try {
            Field membersField = JsonObject.class.getDeclaredField("members");
            membersField.setAccessible(true);
            for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
                ((LinkedTreeMap<String, JsonElement>) membersField.get(jsonObject)).put(entry.getKey(), entry.getValue());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    @Override
    public boolean isJsonObject() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof JsonCompound
                && ((JsonCompound) o).members.equals(members));
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
