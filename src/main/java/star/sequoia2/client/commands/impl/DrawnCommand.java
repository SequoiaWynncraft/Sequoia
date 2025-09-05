package star.sequoia2.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.NotificationsAccessor;
import star.sequoia2.client.commands.Command;
import star.sequoia2.client.commands.arguments.FeaturesArgumentType;
import star.sequoia2.features.ToggleFeature;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import star.sequoia2.features.Feature;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static star.sequoia2.client.commands.arguments.FeaturesArgumentType.getModule;

public class DrawnCommand extends Command implements FeaturesAccessor, NotificationsAccessor {
    public DrawnCommand() {
        super("drawn", "Display feature in arraylist", List.of("feature"));
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .then(argument("feature", FeaturesArgumentType.module())
                .executes(context -> {
                    Feature feature = getModule(context, "feature");
                        if (feature instanceof ToggleFeature toggleModule) {
                            toggleModule.setDrawn(!toggleModule.getDrawn());
                            notifications().sendMessage(
                                Text.of(String.format(
                                        "Drawing for %s has been set to %b",
                                        feature.getName(), toggleModule.getDrawn()
                                )), "Drawing for"
                            );
                        } else {
                            notifications().sendMessage(Text.of("This is not a ToggleFeature."));
                        }
                    return SINGLE_SUCCESS;
                })
            );
    }
}
