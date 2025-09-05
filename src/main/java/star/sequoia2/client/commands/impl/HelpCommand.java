package star.sequoia2.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.NotificationsAccessor;
import star.sequoia2.client.NectarClient;
import star.sequoia2.client.commands.Command;
import star.sequoia2.features.impl.Client;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class HelpCommand extends Command implements NotificationsAccessor, FeaturesAccessor {

    public HelpCommand() {
        super("help", "List all commands", List.of());
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .executes(context -> {
                StringBuilder commandDetails = new StringBuilder();
                commandDetails.append("List of commands:").append("\n");
                for (Command command : NectarClient.getCommands().getCommandsList()) {
                    commandDetails.append(Formatting.WHITE).append(feature(Client.class).getPrefix()).append(command.getName());

                    if (!command.getArguments().isEmpty()) {
                        commandDetails.append(" ").append(command.getArguments());
                    }

                    commandDetails.append(Formatting.GRAY).append(" | ").append(command.getDescription()).append("\n");
                }
                notifications().sendMessage(Text.of(commandDetails.toString()));
                return SINGLE_SUCCESS;
            });

    }
}