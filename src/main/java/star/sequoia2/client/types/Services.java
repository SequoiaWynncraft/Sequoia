package star.sequoia2.client.types;

import star.sequoia2.client.services.mojang.MojangService;
import star.sequoia2.client.services.wynn.guild.GuildService;
import star.sequoia2.client.services.wynn.player.PlayerService;

public final class Services {
    public static final PlayerService Player = new PlayerService();
    public static final GuildService Guild = new GuildService();
    public static final MojangService Mojang = new MojangService();

    private Services() {}
}