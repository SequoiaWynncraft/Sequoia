package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import star.sequoia2.client.types.command.Command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MeowCommand extends Command {
    @Override
    public String getCommandName() {
        return "meow";
    }


    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName()).executes(this::meow
                )
        );

    }

    private int meow(CommandContext<FabricClientCommandSource> ctx) {
        var player = ctx.getSource().getPlayer();               // returns ClientPlayerEntity
        if (player == null) return 0;                           // not connected / in title screen

        var network = player.networkHandler;

        network.sendChatMessage("/g meow");

        return 1;
    }
}
