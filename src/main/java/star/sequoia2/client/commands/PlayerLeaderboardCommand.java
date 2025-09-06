package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.Services;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.PlayerSuggestionProvider;
import star.sequoia2.utils.MinecraftUtils;

import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayerLeaderboardCommand extends Command implements TeXParserAccessor {
    @Override
    public String getCommandName() {
        return "leaderboard";
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName())
                        .then(argument("player", word()).suggests(new PlayerSuggestionProvider())
                            .executes(ctx -> {return lookupPlayer(ctx, 5);}
                            )
                            .then(argument("results", integer())
                                .executes(ctx -> {
                                return lookupPlayer(ctx, ctx.getArgument("results", Integer.class));
                            })
                        )
                        )
        );
    }
    @Override
    public List<String> getAliases() {
        return List.of("lb");
    }
    private int lookupPlayer(CommandContext<FabricClientCommandSource> ctx, Integer results) {

        String username = ctx.getArgument("player", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            ctx.getSource()
                    .sendError(SeqClient.prefix(Text.translatable("sequoia.command.invalidUsername")));
            return 0;
        } else {
            Services.Player.getPlayer(username).whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    SeqClient.error("Error looking up player: " + username, throwable);
                    ctx.getSource()
                            .sendError(SeqClient.prefix(Text.translatable(
                                    "sequoia.command.playerLeaderboard.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null
                            || playerResponse.getGlobalData() == null
                            || playerResponse.getGlobalData().getRaids() == null) {
                        ctx.getSource()
                                .sendError(SeqClient.prefix(Text.translatable(
                                        "sequoia.command.playerLeaderboard.playerNotFound", username)));
                    } else {
                        ctx.getSource()
                                .sendFeedback(
                                        SeqClient.prefix(
                                                teXParser().parseMutableText(I18n.translate("sequoia.command.playerLeaderboard.showingPlayerLeaderboard", playerResponse.getUsername(), results))
                                                        .append(playerResponse.rankingPrettyMessage(playerResponse.getRanking(), results))
                                        )
                                );
                    }
                }

            });
        }

//                        ctx.getSource().sendFeedback(Text.translatable("sequoia.command.invalidUsername").formatted(Formatting.BLUE));
        return 1;

    }

}
