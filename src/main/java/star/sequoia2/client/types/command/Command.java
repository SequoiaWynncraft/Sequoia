package star.sequoia2.client.types.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.wynntils.core.persisted.Translatable;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.List;

public abstract class Command implements Translatable {

    public abstract String getCommandName();

    public abstract CommandNode<FabricClientCommandSource>  register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access);

    @Override
    public String getTypeName() {
        return "Command";
    }

    public List<String> getAliases() {
        return List.of();
    }

    public void registerAll(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        CommandNode<FabricClientCommandSource> root = register(dispatcher, access);
        for (String alias : getAliases()) {
            dispatcher.register(
                    ClientCommandManager.literal(alias)
                            .redirect(root)
            );
        }


    }
}
