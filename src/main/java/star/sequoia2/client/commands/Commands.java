package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Commands {

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        new PlayerRaidsCommand().registerAll(dispatcher, access);
        new ConfigCommand().registerAll(dispatcher, access);
//        new OnlineMembersCommand().registerAll(dispatcher, access);
        new PlayerWarsCommand().registerAll(dispatcher, access);
        new PlayerLeaderboardCommand().registerAll(dispatcher, access);
        new MeowCommand().registerAll(dispatcher, access);
        new GuildInfoCommand().registerAll(dispatcher, access);
        new CompareCommand().registerAll(dispatcher, access);
        new ConnectCommand().registerAll(dispatcher, access);
        new DisconnectCommand().registerAll(dispatcher, access);
        new PlayerInfoCommand().registerAll(dispatcher, access);
        new PlayerCharactersCommand().registerAll(dispatcher, access);
        new AuthCommand().registerAll(dispatcher, access);
        new DummyCommand().registerAll(dispatcher, access);
        new ClientCommand().registerAll(dispatcher, access);
    }
}
