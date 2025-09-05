package star.sequoia2.client.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import star.sequoia2.accessors.NotificationsAccessor;
import star.sequoia2.client.commands.Command;
import star.sequoia2.client.commands.arguments.FeaturesArgumentType;
import star.sequoia2.client.commands.arguments.SettingArgumentType;
import star.sequoia2.features.Feature;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.settings.CommandSupport;
import star.sequoia2.settings.Setting;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static star.sequoia2.client.commands.arguments.FeaturesArgumentType.getModule;
import static star.sequoia2.client.commands.arguments.SettingArgumentType.getSetting;

public class ModuleCommand extends Command implements NotificationsAccessor {
    public ModuleCommand() {
        super("feature", "Change feature settings", List.of("feature", "setting", "value"));
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .then(argument("feature", FeaturesArgumentType.module())
                .executes(context -> {
                    Feature feature = getModule(context, "feature");
                    if (feature instanceof ToggleFeature toggleModule) {
                        toggleModule.toggle();
                    } else {
                        notifications().sendMessage(Text.of(Formatting.RED + feature.name + " cannot be toggled."), feature.name);
                    }
                    return 1;
                })
                .then(argument("setting", SettingArgumentType.create())
                    .executes(context -> {
                        Feature feature = getModule(context, "feature");
                        Setting<?> setting = getSetting(context, "setting", feature);
                        CommandSupport commandSupport = (CommandSupport)setting;
                        notifications().sendMessage(Text.literal(feature.name + "->" + setting.name + " is currently: " + commandSupport.toPrintableValue()), feature.name);
                        return 1;
                    })
                    .then(argument("value", StringArgumentType.greedyString())
                        .executes(context -> {
                            Feature feature = getModule(context, "feature");
                            Setting<?> setting = getSetting(context, "setting", feature);
                            CommandSupport commandSupport = (CommandSupport)setting;

                            String value = getString(context, "value");
                            if (value.equalsIgnoreCase("reset")) {
                                setting.reset();
                                notifications().sendMessage(Text.literal(feature.name + "->" + setting.name + " reset to " + commandSupport.toPrintableValue()), feature.name);
                            } else {
                                commandSupport.parseValueFromCommand(value);
                                notifications().sendMessage(Text.literal(feature.name + "->" + setting.name + " set to " + commandSupport.toPrintableValue()), feature.name);
                            }
                            return 1;
                        })
                    )
                )
            );
    }
}
