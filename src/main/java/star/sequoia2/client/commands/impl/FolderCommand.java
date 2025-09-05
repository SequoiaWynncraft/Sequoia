package star.sequoia2.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.commands.Command;
import net.minecraft.command.CommandSource;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FolderCommand extends Command {

    public static final Logger LOGGER = LogUtils.getLogger();

    public FolderCommand() {
        super("folder", "Opens config folder.", List.of());
    }

    @Override
    protected void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            try {
                File configFolder = SeqClient.getConfiguration().configDirectory();

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(configFolder);
                } else {
                    String osName = System.getProperty("os.name").toLowerCase();
                    if (osName.contains("win")) {
                        Runtime.getRuntime().exec("explorer " + configFolder.getAbsolutePath());
                    } else if (osName.contains("mac")) {
                        Runtime.getRuntime().exec("open " + configFolder.getAbsolutePath());
                    } else {
                        Runtime.getRuntime().exec("xdg-open " + configFolder.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Failed to open folder: {}", e.getMessage());
            }
            return 1;
        });
    }
}
