package star.sequoia2.client.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import star.sequoia2.accessors.FeaturesAccessor;
import net.minecraft.command.CommandSource;
import star.sequoia2.features.Feature;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FeaturesArgumentType implements ArgumentType<Feature>, FeaturesAccessor {

    @Override
    public Feature parse(StringReader reader) {
        String name = reader.readUnquotedString();
        return features().all().filter(module -> module.commandName().toLowerCase().startsWith(name.toLowerCase())).findFirst().orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (Feature feature : features().all().toList()) {
            builder = builder.suggest(feature.commandName());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return features().all().limit(3).map(Feature::commandName).collect(Collectors.toSet());
    }

    public static FeaturesArgumentType module() {
        return new FeaturesArgumentType();
    }

    public static Feature getModule(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, Feature.class);
    }
}