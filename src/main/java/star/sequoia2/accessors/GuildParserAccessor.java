package star.sequoia2.accessors;

import star.sequoia2.client.SeqClient;
import star.sequoia2.utils.chatparser.GuildMessageParser;
import star.sequoia2.utils.chatparser.GuildRaidParser;

public interface GuildParserAccessor {
    default GuildMessageParser guildMessageParser() {
        return SeqClient.getGuildMessageParser();
    }

    default GuildRaidParser guildRaidParser() {
        return SeqClient.getGuildRaidParser();
    }
}
