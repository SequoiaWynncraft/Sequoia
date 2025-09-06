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
        //open
    }

    private int tryOpenConfigGUI(CommandContext<FabricClientCommandSource> context) {
    tryOpenConfigGUI();
        return 1;
    }

}
