package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import star.sequoia2.client.SeqClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import star.sequoia2.client.commands.impl.*;

import java.util.ArrayList;

public class Commands {
    private static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static final CommandSource COMMAND_SOURCE = new ClientCommandSource(null, SeqClient.mc);
    public static final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();

    public Commands() {
        addCommand(new HelpCommand());
        addCommand(new DrawnCommand());
        addCommand(new ModuleCommand());
        addCommand(new FolderCommand());
        addCommand(new ConfigCommand());
    }

    public void addCommand(Command command) {
        COMMANDS.removeIf(existing -> existing.getName().equals(command.getName()));
        command.registerTo(DISPATCHER);
        COMMANDS.add(command);
    }

    public void dispatch(String message) throws CommandSyntaxException {
        DISPATCHER.execute(message, COMMAND_SOURCE);
    }

    public ArrayList<Command> getCommandsList() {
        return COMMANDS;
    }

    public Command getCommand(String name) {
        for (Command command : COMMANDS) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }
}
