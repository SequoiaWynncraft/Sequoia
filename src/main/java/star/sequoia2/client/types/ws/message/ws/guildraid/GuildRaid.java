package star.sequoia2.client.types.ws.message.ws.guildraid;

import java.util.List;
import java.util.UUID;

public record GuildRaid(
        RaidType type, List<String> players, UUID reporterID, long aspects, long emeralds, long xp, long sr) {}