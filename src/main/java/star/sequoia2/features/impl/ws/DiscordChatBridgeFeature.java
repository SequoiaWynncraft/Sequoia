package star.sequoia2.features.impl.ws;

import lombok.Getter;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.settings.types.BooleanSetting;

public class DiscordChatBridgeFeature extends ToggleFeature {

    @Getter
    BooleanSetting sendDiscordMessageToChat = settings().bool("ShowDiscordInChat", "test", true);

    public DiscordChatBridgeFeature() {
        super("DiscordChatBridge", "forwards messages to discord", true);
    }
}
