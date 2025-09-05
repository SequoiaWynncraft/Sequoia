package star.sequoia2.configuration;

import com.google.common.base.StandardSystemProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static star.sequoia2.client.SeqClient.mc;

@Getter
public final class Configuration {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private final JsonCompound features;

    public Configuration() throws IOException {
        this.features = fromFile(configFile());
    }

    public void save() throws IOException {
        try (FileWriter writer = new FileWriter(configFile())) {
            GSON.toJson(this.features.getAsJsonObject(), writer);
        }
    }

    private static JsonCompound fromFile(File file) throws IOException {
        if (file.exists() && JsonParser.parseReader(new FileReader(file)) instanceof JsonObject json) {
            return JsonCompound.wrap(json);
        }
        return new JsonCompound();
    }

    private File configFile() {
        return new File(mc.runDirectory, "seq.json");
    }

    public File configsFolder() throws IOException {
        File configsFolder = new File(configDirectory(), "configs");
        if (!configsFolder.exists()) {
            if (!configsFolder.mkdirs()) {
                throw new RuntimeException("Failed to create directory \"" + configsFolder.getName() + "\"");
            }
        }
        return configsFolder;
    }

    public File configDirectory() throws IOException {
        String userHome = StandardSystemProperty.USER_HOME.value();
        String osName = StandardSystemProperty.OS_NAME.value().toLowerCase(Locale.ROOT);

        Path configDirPath;
        if (userHome != null) {
            if (osName.contains("win")) {
                configDirPath = Paths.get(userHome, "seq");
            } else if (osName.contains("mac")) {
                configDirPath = Paths.get(userHome, "Library", "Application Support", "seq");
            } else {
                String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
                if (xdgConfigHome != null && !xdgConfigHome.isEmpty()) {
                    configDirPath = Paths.get(xdgConfigHome, "seq");
                } else {
                    configDirPath = Paths.get(userHome, ".config", "seq");
                }
            }

            if (!Files.exists(configDirPath)) {
                Files.createDirectories(configDirPath);
            }

            return configDirPath.toFile();
        } else return new File(mc.runDirectory, "seq"); //legacy way if chink method doesn't work (tbf default.json should be per instance cuz multiple instances use same config with the current system, or just make configs loadable/saveable)
    }

}