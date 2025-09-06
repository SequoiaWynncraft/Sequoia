package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.wynntils.core.components.Managers;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.SuggestionProviders;
import star.sequoia2.utils.wynn.WynnUtils;

import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static star.sequoia2.utils.AccessTokenManager.invalidateAccessToken;

public class DisconnectCommand extends Command {
    @Override
    public String getCommandName() {
        return "disconnect";
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                ClientCommandManager.literal(getCommandName())
                        .executes(this::auth)
                        .then(argument("deletetoken", word())
                                .suggests(SuggestionProviders.DELETE_TOKEN_SUGGESTION_PROVIDER)
                                .executes(this::auth))
        );

    }

    public static void deleteToken() {
        try {
            invalidateAccessToken();
            SeqClient.debug(I18n.translate("sequoia.command.deletetoken.success"));
        } catch (Exception exception) {
            SeqClient.debug(I18n.translate("sequoia.command.deletetoken.error"));
        }
    }

    public void deleteToken(CommandContext<FabricClientCommandSource> ctx) {
        try {
            invalidateAccessToken();
            ctx.getSource()
                    .sendFeedback(
                            SeqClient.prefix(Text.translatable("sequoia.command.deletetoken.success")));
        } catch (Exception exception) {
            ctx.getSource()
                    .sendError(
                            SeqClient.prefix(Text.translatable("sequoia.command.deletetoken.error")));
        }
    }

    private void sorter(CommandContext<FabricClientCommandSource> ctx) {
        if (!SeqClient.getWebSocketFeature().isEnabled()) {
            ctx.getSource()
                    .sendError(
                            SeqClient.prefix(Text.translatable("sequoia.feature.webSocket.featureDisabled")));
            return;
        }

        WynnUtils.isSequoiaGuildMember()
                .whenComplete((isMember, ex) -> MinecraftClient.getInstance().execute(() -> {
                    if (ex != null || !Boolean.TRUE.equals(isMember)) {
                        ctx.getSource().sendError(
                                SeqClient.prefix(Text.translatable("sequoia.command.notASequoiaGuildMember")));
                        return;
                    }

                    if (SeqClient.getWebSocketFeature().getClient() == null
                            || !SeqClient.getWebSocketFeature().getClient().isOpen()) {
                        ctx.getSource()
                                .sendError(SeqClient.prefix(Text.translatable("sequoia.command.disconnect.notConnected")));
                        return;
                    }

                    ctx.getSource()
                            .sendFeedback(
                                    SeqClient.prefix(Text.translatable("sequoia.command.disconnect.disconnecting"))

                            );
                    SeqClient.getWebSocketFeature().closeIfNeeded();
                    Managers.TickScheduler.scheduleLater(
                            () -> {
                                if (SeqClient.getWebSocketFeature().getClient().isClosed()) {
                                    ctx.getSource()
                                            .sendFeedback(
                                                    SeqClient.prefix(
                                                            Text.translatable("sequoia.command.disconnect.disconnected"))
                                            );
                                    return;
                                }

                                ctx.getSource()
                                        .sendError(SeqClient.prefix(
                                                Text.translatable("sequoia.command.disconnect.failedToDisconnect")));
                            },
                            5);
                }));
    }

    private int auth(CommandContext<FabricClientCommandSource> ctx) {
        sorter(ctx);

        if (Objects.equals(ctx.getInput(), "disconnect deletetoken")) {
            deleteToken(ctx);
        }
        return 1;
    }
}
