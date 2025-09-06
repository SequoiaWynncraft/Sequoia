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
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.PlayerSuggestionProvider;
import star.sequoia2.utils.MinecraftUtils;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayerWarsCommand extends Command implements TeXParserAccessor {
    @Override
    public String getCommandName() {
        return "wars";
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName())
                        .then(argument("player", word()).suggests(new PlayerSuggestionProvider())
                                .executes(this::lookupPlayerWars
                                )
                        )
        );
    }
    @Override
    public List<String> getAliases() {
        return List.of("ws");
    }

    private int lookupPlayerWars(CommandContext<FabricClientCommandSource> context) {
        String username = context.getArgument("player", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            context.getSource()
                    .sendError(SeqClient.prefix(Text.translatable("sequoia.command.invalidUsername")));
        } else {
            Services.Player.getPlayer(username).whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    SeqClient.error("Error looking up player: " + username, throwable);
                    context.getSource()
                            .sendError(SeqClient.prefix(Text.translatable(
                                    "sequoia.command.playerWars.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null) {
                        context.getSource()
                                .sendError(SeqClient.prefix(
                                        Text.translatable("sequoia.command.playerWars.playerNotFound", username)));
                    } else {
                            context.getSource()
                                    .sendFeedback(
                                            SeqClient.prefix(
                                                    teXParser().parseMutableText(I18n.translate("sequoia.command.playerWars.showingPlayerWars",
                                                            playerResponse.getUsername(), playerResponse.getGlobalData().getWars(), playerResponse.getRanking().get("warsCompletion")
                                                    ))
//                                                    Text.literal(playerResponse.getUsername()).styled(selectedTheme.accent1())
//                                                    .append(Text.translatable("sequoia.command.playerWars.showingPlayerWars1")).styled(selectedTheme.light())
//                                                    .append(Text.literal(Integer.toString(playerResponse.getGlobalData().getWars())).styled(selectedTheme.accent1()))
//                                                    .append(Text.translatable("sequoia.command.playerWars.showingPlayerWars2")).styled(selectedTheme.light())
//                                                    .append(Text.literal(" (")).styled(selectedTheme.light())
//                                                    .append(Text.literal("#" + playerResponse.getRanking().get("warsCompletion")).styled(selectedTheme.accent2()))
//                                                    .append(Text.literal(")")).styled(selectedTheme.light())
                                            ));
//                                                    Text.translatable(
//                                                    "sequoia.command.playerWars.showingPlayerWars",
//                                                    playerResponse.getUsername(),
//                                                    playerResponse
//                                                            .getGlobalData()
//                                                            .getWars(),
//                                                    playerResponse.getRanking().get("warsCompletion"))));
                    }
                }
            });
        }
        return 1;
    }

}
