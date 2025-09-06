package star.sequoia2.utils.cache;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import star.sequoia2.Sequoia2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil.GSON;

public final class GuildCache  {
    private static final Path FILE = MinecraftClient.getInstance().runDirectory
            .toPath().resolve("sequoia/cache/guilds.json");
    private static final Duration MAX_AGE = Duration.ofHours(1);

    private static Map<String,String> nameToPrefix = Map.of();
    private static Map<String,String> prefixToName = Map.of();
    private static Map<@NotNull String, @NotNull String> canonical;
    static List<String> ids;
    static List<String> prefixOnlyIds;


    public static void init() {
        try {
            if (Files.exists(FILE) &&
                    Instant.now().minus(MAX_AGE).isBefore(Files.getLastModifiedTime(FILE).toInstant())) {
                loadFromDisk();
            } else {
                fetchFromApi();          // downloads + writes to disk
            }
            buildCanonical();

            // names + prefixes, read-only copy
            List<String> ids = new ArrayList<>(nameToPrefix.keySet());
            ids.addAll(prefixToName.keySet());
            GuildCache.ids = ids;

            List<String> prefixOnlyIds = new ArrayList<>(prefixToName.keySet());
            GuildCache.prefixOnlyIds = prefixOnlyIds;

            Sequoia2.debug("Initialising guild cache");
        } catch (Exception e) {
            Sequoia2.error("Failed initialising guild cache", e);
        }
    }

    public static Optional<String> canonicalName(String input) {
        if (input == null) return Optional.empty();

        String key = input.trim();

        if (nameToPrefix.containsKey(key) || prefixToName.containsKey(key)) {
            return Optional.of(key);
        }

        return Optional.ofNullable(
                canonical.get(key.toLowerCase(Locale.ROOT))
        );
    }

    private static void buildCanonical() {
        Map<String,String> map = new HashMap<>();
        nameToPrefix.keySet().forEach(n -> map.put(n.toLowerCase(Locale.ROOT), n));
        prefixToName.keySet().forEach(p -> map.putIfAbsent(p.toLowerCase(Locale.ROOT), p));
        canonical = Map.copyOf(map);
    }


    private static void loadFromDisk() throws IOException {
        JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
        nameToPrefix = GSON.fromJson(root.get("nameToPrefix"), Map.class);
        prefixToName = GSON.fromJson(root.get("prefixToName"), Map.class);
    }

    static void fetchFromApi() throws IOException, InterruptedException, ExecutionException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.wynncraft.com/v3/guild/list/guild"))
                .GET().build();
//        var body = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString()).body();
//      JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        var body = HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString()).get().body();
      JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        Map<String,String> n2p = new HashMap<>();
        Map<String,String> p2n = new HashMap<>();
        json.entrySet().forEach(e -> {
            String name  = e.getKey();
            String prefix = e.getValue().getAsJsonObject().get("prefix").getAsString();
            n2p.put(name,  prefix);
            p2n.put(prefix, name);
        });

        nameToPrefix = Map.copyOf(n2p);
        prefixToName = Map.copyOf(p2n);

        // write the cache file (pretty-printed for easy reading)
        Files.createDirectories(FILE.getParent());
        Files.writeString(FILE,
                new GsonBuilder().setPrettyPrinting().create().toJson(
                        Map.of("nameToPrefix", n2p, "prefixToName", p2n, "fetched", Instant.now().getEpochSecond())
                ));
    }

    public static Collection<String> allIdentifiers() {
        // names + prefixes, read-only copy
        return ids;
    }

    public static Collection<String> prefixIdentifiers() {
        // names + prefixes, read-only copy
        return prefixOnlyIds;
    }
}
