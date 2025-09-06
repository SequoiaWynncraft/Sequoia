package star.sequoia2.client.types.command.suggestions;

// https://docs.fabricmc.net/develop/commands/suggestions

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static star.sequoia2.utils.MinecraftUtils.isValidUsername;


public class PlayerSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        FabricClientCommandSource source = context.getSource();

        Collection<String> playerNames = source.getPlayerNames();

        for (String playerName : playerNames) {
            if (isValidUsername(playerName)) {
                builder.suggest(playerName);
            }
        }

        return builder.buildFuture();
    }
}

