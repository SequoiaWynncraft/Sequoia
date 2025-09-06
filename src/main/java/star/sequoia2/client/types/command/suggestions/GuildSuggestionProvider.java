package star.sequoia2.client.types.command.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import star.sequoia2.utils.cache.GuildCache;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class GuildSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {

        // text the player has typed so far (case-insensitive match)
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

        /* GuildCacheUtils exposes every known
           name and tag through one helper.  */
        GuildCache.allIdentifiers().stream()
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(remaining))
                .sorted()
                .forEach(builder::suggest);


        return builder.buildFuture();
    }


    public CompletableFuture<Suggestions> getPrefixSuggestions(
            CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {

        // text the player has typed so far (case-insensitive match)
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

        /* GuildCacheUtils exposes every known
           name and tag through one helper.  */
        GuildCache.prefixIdentifiers().stream()
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(remaining))
                .sorted()
                .forEach(builder::suggest);


        return builder.buildFuture();
    }
}
