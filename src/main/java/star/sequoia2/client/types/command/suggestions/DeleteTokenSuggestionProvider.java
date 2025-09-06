package star.sequoia2.client.types.command.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.concurrent.CompletableFuture;

public class DeleteTokenSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {

    builder.suggest("deletetoken");


        return builder.buildFuture();
    }
}
