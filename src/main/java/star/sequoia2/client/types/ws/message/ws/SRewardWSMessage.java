package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.JsonElement;
import star.sequoia2.client.types.ws.message.WSMessage;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SRewardWSMessage extends WSMessage {
    public SRewardWSMessage(int type, JsonElement data) {
        super(type, data);
    }

    public SChannelMessageWSMessage.Data getSChannelMessageData() {
        return GSON.fromJson(getData(), SChannelMessageWSMessage.Data.class);
    }
    public record Data(
            int emeralds,
            int aspects,
            int tomes
    ){
        public boolean emeraldsAreFull(){
            return emeralds == 30720;
        }
        public boolean aspectsAreFull(){
            return aspects == 40;
        }
        public boolean tomesAreFull(){
            return tomes == 10;
        }
    }
}