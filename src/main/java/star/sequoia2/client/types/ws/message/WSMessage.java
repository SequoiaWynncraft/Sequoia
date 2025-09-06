package star.sequoia2.client.types.ws.message;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class WSMessage {
    private final int type;
    private final JsonElement data;

    public WSMessage(int type, JsonElement data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public JsonElement getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WSMessage wsMessage = (WSMessage) o;

        return new EqualsBuilder()
                .append(type, wsMessage.type)
                .append(data, wsMessage.data)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(type).append(data).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("data", data)
                .toString();
    }
}