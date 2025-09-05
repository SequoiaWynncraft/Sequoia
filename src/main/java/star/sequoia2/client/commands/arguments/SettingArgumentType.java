package star.sequoia2.client.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.logging.LogUtils;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.features.Feature;
import star.sequoia2.settings.CommandSupport;
import star.sequoia2.settings.Setting;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static star.sequoia2.client.commands.arguments.FeaturesArgumentType.getModule;

public class SettingArgumentType implements ArgumentType<Setting<?>>, FeaturesAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public Setting<?> parse(StringReader reader) {
        String name = reader.readUnquotedString();
        return features().all().flatMap(module -> module.settingsState().fromFeature(module).all())
                .filter(setting -> setting instanceof CommandSupport)
                .filter(setting -> ((CommandSupport) setting).commandName().toLowerCase().startsWith(name)).findFirst()
                .orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Feature feature;
        try {
            feature = getModule((CommandContext<CommandSource>) context, "feature");
        } catch (CommandSyntaxException e) {
            LOGGER.warn("Could not find feature variable", e);
            return builder.buildFuture();
        }
        for (Setting<?> setting : feature.settingsState().fromFeature(feature).all().filter(setting -> setting instanceof CommandSupport).toList()) {
            builder = builder.suggest(((CommandSupport)setting).commandName());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return Set.of();
    }

    public static SettingArgumentType create() {
        return new SettingArgumentType();
    }

    public static Setting<?> getSetting(CommandContext<CommandSource> context, String name, Feature feature) throws CommandSyntaxException {
        return context.getArgument(name, Setting.class);
    }
}
