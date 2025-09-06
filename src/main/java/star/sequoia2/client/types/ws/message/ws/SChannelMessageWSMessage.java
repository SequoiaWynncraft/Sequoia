package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.annotations.SerializedName;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SChannelMessageWSMessage extends WSMessage {
    public SChannelMessageWSMessage(Data data) {
        super(WSMessageType.S_CHANNEL_MESSAGE.getValue(), GSON.toJsonTree(data));
    }

    public Data getSChannelMessageData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            String username,
            String nickname,
            @SerializedName("display_name") String displayName,
            @SerializedName("sequoia_roles") Map<String, List<Integer>> sequoiaRoles,
            List<Integer> color,
            String message,
            OffsetDateTime timestamp) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return username.equals(data.username)
                    && nickname.equals(data.nickname)
                    && displayName.equals(data.displayName)
                    && Objects.equals(sequoiaRoles, data.sequoiaRoles)
                    && message.equals(data.message)
                    && timestamp.equals(data.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, nickname, displayName, sequoiaRoles, message, timestamp);
        }

        @Override
        public String toString() {
            return "Data{" +
                    "username='" + username + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", sequoiaRoles=" + sequoiaRoles +
                    ", message='" + message + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}