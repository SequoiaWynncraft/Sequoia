package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.ws.message.ws.GClientCommandWSMessage;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientCommand extends Command {
    @Override
    public String getCommandName() {
        return "seq";
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                                           CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName())
                        // /seq <cmd>
                        .then(ClientCommandManager.argument("cmd", StringArgumentType.word())
                                .executes(ctx -> sendClientCommand(ctx,
                                        StringArgumentType.getString(ctx, "cmd"),
                                        "" // no args
                                ))
                                // /seq <cmd> <args...>
                                .then(ClientCommandManager.argument("args", StringArgumentType.greedyString())
                                        .executes(ctx -> sendClientCommand(ctx,
                                                StringArgumentType.getString(ctx, "cmd"),
                                                StringArgumentType.getString(ctx, "args")
                                        ))
                                )
                        )
        );
    }

    private int sendClientCommand(CommandContext<FabricClientCommandSource> ctx, String cmd, String args) {
        if (!SeqClient.getWebSocketFeature().isEnabled()) {
            ctx.getSource().sendError(
                    SeqClient.prefix(Text.translatable("sequoia.feature.webSocket.featureDisabled")));
            return 1;
        }

        WebSocketClient ws = SeqClient.getWebSocketFeature().getClient();
        if (ws == null || !ws.isOpen()) {
            ctx.getSource().sendError(
                    SeqClient.prefix(Text.translatable("sequoia.command.ws.notConnected")));
            return 1;
        }

        // Build and send the message
        GClientCommandWSMessage.Data payload = new GClientCommandWSMessage.Data(cmd, args);
        GClientCommandWSMessage message = new GClientCommandWSMessage(payload);

        SeqClient.getWebSocketFeature().sendMessage(message);

        return 1;
    }
}
