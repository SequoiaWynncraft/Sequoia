package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.screen.ClickGUIScreen;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCommand extends Command implements FeaturesAccessor {
    @Override
    public String getCommandName() {
        return "seqconfig";
    }


    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName()).executes(this::tryOpenConfigGUI)
        );

    }

    public int tryOpenConfigGUI(CommandContext<FabricClientCommandSource> ctx){
        return 1;
    }
}
