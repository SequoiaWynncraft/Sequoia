package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandRegistryAccess;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.command.Command;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCommand extends Command {
    @Override
    public String getCommandName() {
        return "seqconfig";
    }


    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName()).executes(this::tryOpenConfigGUI
                )
        );

    }public static void tryOpenConfigGUI(){
        try {
            Class<?> configScreenProvidersClass = Class.forName("io.wispforest.owo.config.ui.ConfigScreenProviders");
            Method getMethod = configScreenProvidersClass.getMethod("get", String.class);
            Function<Object, ConfigScreen> configScreenProvider =
                    (Function<Object, ConfigScreen>) getMethod.invoke(null, SeqClient.MOD_ID);

            if (configScreenProvider == null) {
                SeqClient.error("No ConfigScreenProvider found for mod ID: " + SeqClient.MOD_ID
                        + ", do you have owo-lib installed?");
            }

            Object screen = configScreenProvider.apply(null);
            if (screen instanceof Screen) {
                SeqClient.debug("Attempting to open ConfigScreen for mod ID: " + SeqClient.MOD_ID);
                Executors.newSingleThreadScheduledExecutor()
                        .schedule(
                                () -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance()
                                        .setScreen((Screen) screen)),
                                1,
                                TimeUnit.MILLISECONDS);
            } else {
                SeqClient.error("ConfigScreenProvider returned unexpected Screen type: "
                        + screen.getClass().getName());
            }
        } catch (Exception exception) {
            SeqClient.error("Failed to open ConfigScreen", exception);
        }
    }

    private int tryOpenConfigGUI(CommandContext<FabricClientCommandSource> context) {
    tryOpenConfigGUI();
        return 1;
    }

}
