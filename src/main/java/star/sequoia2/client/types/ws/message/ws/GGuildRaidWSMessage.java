package star.sequoia2.client.types.ws.message.ws;

import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.message.ws.guildraid.GuildRaid;
import star.sequoia2.client.types.ws.type.WSMessageType;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class GGuildRaidWSMessage extends WSMessage {
    public GGuildRaidWSMessage(GuildRaid guildRaid) {
        super(WSMessageType.G_RAID_SUBMISSION.getValue(), GSON.toJsonTree(guildRaid));
    }
}
