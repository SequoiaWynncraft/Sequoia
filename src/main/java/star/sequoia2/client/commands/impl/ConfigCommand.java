package star.sequoia2.client.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import star.sequoia2.accessors.ConfigurationAccessor;
import star.sequoia2.accessors.NotificationsAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static star.sequoia2.client.SeqClient.mc;

public class ConfigCommand extends Command implements NotificationsAccessor, ConfigurationAccessor {
    public ConfigCommand() {
        super("config", "Save / load configs", List.of("load", "save", "list", "delete"), "cfg");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .executes(this::help)
            .then(literal("list").executes(this::list))
            .then(literal("save")
                .then(argument("name", StringArgumentType.greedyString()).executes(this::save)))
                .then(literal("load")
                    .then(argument("name", StringArgumentType.greedyString()).suggests((context, suggestionsBuilder) -> {
                        File configsFolder = null;
                        try {
                            configsFolder = configuration().configsFolder();
                        } catch (IOException e) {
                            notifications().sendMessage(Text.of("No config folder found."));
                        }
                        String[] files = configsFolder.list((dir, name) -> name.toLowerCase().endsWith(".json"));
                        if (files != null) {
                            List<String> suggestions = Arrays.stream(files)
                                    .map(name -> name.substring(0, name.length() - 5))
                                    .collect(Collectors.toList());
                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        }
                        return suggestionsBuilder.buildFuture();
                    }).executes(this::load)))
                .then(literal("delete")
                    .then(argument("name", StringArgumentType.greedyString()).suggests((context, suggestionsBuilder) -> {
                        File configsFolder = null;
                        try {
                            configsFolder = configuration().configsFolder();
                        } catch (IOException e) {
                            notifications().sendMessage(Text.of("No config folder found."));
                        }
                        String[] files = configsFolder.list((dir, name) -> name.toLowerCase().endsWith(".json"));
                        if (files != null) {
                            List<String> suggestions = Arrays.stream(files)
                                    .map(name -> name.substring(0, name.length() - 5))
                                    .collect(Collectors.toList());
                            return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
                        }
                        return suggestionsBuilder.buildFuture();
                    }).executes(this::delete)));
    }

    private int help(CommandContext<CommandSource> context) {
        notifications().sendMessage(Text.of("Possible arguments are: save/load/list/delete"));
        return SINGLE_SUCCESS;
    }

    private int list(CommandContext<CommandSource> context) {
        try {
            File configsFolder = configuration().configsFolder();
            String[] files = configsFolder.list((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (files == null || files.length == 0) {
                notifications().sendMessage(Text.of("No saved configs found."));
            } else {
                String list = Arrays.stream(files).collect(Collectors.joining(", "));
                notifications().sendMessage(Text.of("Saved configs: " + list));
            }
        } catch (IOException e) {
            notifications().sendMessage(Text.of("Couldn't find configuration folder."));
        }
        return SINGLE_SUCCESS;
    }

    private int save(CommandContext<CommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if (!name.toLowerCase().endsWith(".json")) {
            name += ".json";
        }
        try {
            File configsFolder = configuration().configsFolder();
            File currentConfig = new File(mc.runDirectory, "seq.json");
            if (!currentConfig.exists()) {
                notifications().sendMessage(Text.of("Current config file does not exist."));
                return SINGLE_SUCCESS;
            }
            File target = new File(configsFolder, name);
            Files.copy(currentConfig.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            notifications().sendMessage(Text.of("Config saved as " + name));
        } catch (IOException e) {
            notifications().sendMessage(Text.of("Failed to save config: " + e.getMessage()));
        }
        return SINGLE_SUCCESS;
    }

    private int load(CommandContext<CommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if (!name.toLowerCase().endsWith(".json")) {
            name += ".json";
        }
        try {
            File configsFolder = configuration().configsFolder();
            File source = new File(configsFolder, name);
            if (!source.exists()) {
                notifications().sendMessage(Text.of("Config " + name + " does not exist."));
                return SINGLE_SUCCESS;
            }
            File currentConfig = new File(mc.runDirectory, "seq.json");
            byte[] data = Files.readAllBytes(source.toPath());
            Files.write(currentConfig.toPath(), data);

            SeqClient.reloadConfiguration();

            SeqClient.getSettings().load(SeqClient.getFeatures());

            notifications().sendMessage(Text.of("Config " + name + " loaded successfully."));
        } catch (IOException e) {
            notifications().sendMessage(Text.of("Failed to load config: " + e.getMessage()));
        }
        return SINGLE_SUCCESS;
    }


    private int delete(CommandContext<CommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        if (!name.toLowerCase().endsWith(".json")) {
            name += ".json";
        }
        try {
            File configsFolder = configuration().configsFolder();
            File target = new File(configsFolder, name);
            if (!target.exists()) {
                notifications().sendMessage(Text.of("Config " + name + " does not exist."));
            } else {
                if (target.delete()) {
                    notifications().sendMessage(Text.of("Config " + name + " deleted successfully."));
                } else {
                    notifications().sendMessage(Text.of("Failed to delete config " + name + "."));
                }
            }
        } catch (IOException e) {
            notifications().sendMessage(Text.of("Error deleting config: " + e.getMessage()));
        }
        return SINGLE_SUCCESS;
    }
}
