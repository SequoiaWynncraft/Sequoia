package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.annotations.SerializedName;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;


public class GIdentifyWSMessage extends WSMessage {
    public GIdentifyWSMessage(Data data) {
        super(WSMessageType.G_IDENTIFY.getValue(), GSON.toJsonTree(data));
    }

    public Data getGIdentifyData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            @SerializedName("access_token") String accessToken,
            String uuid,
            @SerializedName("mod_version") int modVersion) {
        @Override
        public String toString() {
            return "Data{" + "uuid='" + uuid + '\'' + ", modVersion=" + modVersion + '}';
        }
    }
}