package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.wynntils.core.components.Managers;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.ws.message.ws.GAuthWSMessage;
import star.sequoia2.features.impl.ws.WebSocketFeature;
import star.sequoia2.utils.wynn.WynnUtils;

import java.util.regex.Pattern;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class AuthCommand extends Command implements FeaturesAccessor {
    private static final Pattern CODE_PATTERN = Pattern.compile("[a-z0-9]{64}");

    private boolean sentGAuthWSMessage = false;


    @Override
    public String getCommandName() {
        return "seqauth";
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                ClientCommandManager.literal(getCommandName()).then(ClientCommandManager.argument("code", word()).executes(this::auth)
                )
        );

    }

    private int auth(CommandContext<FabricClientCommandSource> ctx) {
        String code = ctx.getArgument("code", String.class);
        if (CODE_PATTERN.matcher(code).matches()) {
            if (features().getIfActive(WebSocketFeature.class).map(WebSocketFeature::isActive).orElse(false)) {
                ctx.getSource()
                        .sendError(
                                SeqClient.prefix(Text.translatable("sequoia.feature.webSocket.featureDisabled")));
                return 1;
            }

            WynnUtils.isSequoiaGuildMember()
                    .whenComplete((isMember, ex) -> MinecraftClient.getInstance().execute(() -> {
                        if (ex != null || !Boolean.TRUE.equals(isMember)) {
                            ctx.getSource().sendError(
                                    SeqClient.prefix(Text.translatable("sequoia.command.notASequoiaGuildMember")));
                            return;
                        }

                        if (features().getIfActive(WebSocketFeature.class).map(WebSocketFeature::isAuthenticated).orElse(false)) {
                            ctx.getSource()
                                    .sendError(
                                            SeqClient.prefix(Text.translatable("sequoia.command.auth.alreadyAuthenticated")));
                            return;
                        }

                        if (sentGAuthWSMessage) {
                            ctx.getSource()
                                    .sendError(SeqClient.prefix(
                                            Text.translatable("sequoia.command.auth.pleaseWaitBeforeRetrying")));
                            return;
                        }

                        if (!features().getIfActive(WebSocketFeature.class).map(webSocketFeature -> webSocketFeature.getClient().isOpen()).orElse(false)) {
                            features().getIfActive(WebSocketFeature.class).ifPresent(WebSocketFeature::connectIfNeeded);
                        }

                        GAuthWSMessage gAuthWSMessage = new GAuthWSMessage(code);
                        features().getIfActive(WebSocketFeature.class).ifPresent(webSocketFeature -> webSocketFeature.sendMessage(gAuthWSMessage));
                        sentGAuthWSMessage = true;
                        ctx.getSource()
                                .sendFeedback(
                                        SeqClient.prefix(Text.translatable("sequoia.command.auth.authenticating")));

                        Managers.TickScheduler.scheduleLater(() -> sentGAuthWSMessage = false, 20 * 10);


                    }));
        } else {
            ctx.getSource()
                    .sendError(SeqClient.prefix(Text.translatable("sequoia.command.auth.invalidCode")));
        }
        return 1;
    }

}
