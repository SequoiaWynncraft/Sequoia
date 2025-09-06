package star.sequoia2.utils.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches Sequoia (SEQ) guild members as {uuid, username} pairs.
 * - Pulls members via Wynncraft Guild(Name) with identifier=uuid
 * - Resolves UUID -> current username via Mojang Session Server
 * - Writes JSON to config/sequoia/sequoia_members.json
 * - Provides O(1) isSequoiaMember(String username) lookups (case-insensitive)
 */
public final class SequoiaMemberCache {

    private SequoiaMemberCache() {}

    private static final String WYNN_GUILD_URL =
            "https://api.wynncraft.com/v3/guild/Sequoia?identifier=uuid";

    private static final String MOJANG_SESSION_URL =
            "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final Path FILE = MinecraftClient.getInstance().runDirectory
            .toPath().resolve("sequoia/cache/sequoia_members.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    // disk format
    public static final class Entry {
        public String uuid;
        public String username; // current username at resolve time
        public long   resolvedAtEpochSec;
    }

    private static final Type ENTRY_LIST = new TypeToken<List<Entry>>(){}.getType();

    // actual maps
    private static volatile Map<String, Entry> byUuid = new ConcurrentHashMap<>(); // key: uuid (no hyphens, lowercase)
    private static volatile Set<String> namesLower = Collections.emptySet();

    public static void init() {
        try {
            Files.createDirectories(FILE.getParent());
            Set<String> uuids = fetchGuildUuids();
            if (uuids.isEmpty()) {
                if (Files.exists(FILE)) {
                    try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                        List<Entry> list = GSON.fromJson(r, ENTRY_LIST);
                        if (list != null) {
                            Map<String, Entry> tmp = new HashMap<>();
                            for (Entry e : list) {
                                if (e == null || e.uuid == null) continue;
                                String u = normalizeUuid(e.uuid);
                                if (u != null) { tmp.put(u, e); }
                            }
                            byUuid = tmp;
                            rebuildNameIndex();
                        }
                    }
                }
                return; // don’t overwrite with empty
            }

            Map<String, Entry> tmp = new HashMap<>();
            Instant now = Instant.now();
            for (String u : uuids) {
                String name = resolveNameFromMojang(u);
                Entry e = new Entry();
                e.uuid = u;
                e.username = name;
                e.resolvedAtEpochSec = now.getEpochSecond();
                tmp.put(u, e);
            }
            byUuid = tmp;
            rebuildNameIndex();
            persist();
        } catch (IOException ignored) {}
    }

    public static boolean isSequoiaMember(String username) {
        if (username == null || username.isEmpty()) return false;
        return namesLower.contains(username.toLowerCase(Locale.ROOT));
    }

    private static void rebuildNameIndex() {
        Set<String> s = new HashSet<>(Math.max(16, byUuid.size() * 2));
        for (Entry e : byUuid.values()) {
            if (e.username != null) s.add(e.username.toLowerCase(Locale.ROOT));
        }
        namesLower = Collections.unmodifiableSet(s);
    }

    private static String normalizeUuid(String raw) {
        if (raw == null) return null;
        String noHyphen = raw.replace("-", "").toLowerCase(Locale.ROOT);
        return noHyphen.matches("^[0-9a-f]{32}$") ? noHyphen : null;
    }

    private static String resolveNameFromMojang(String uuidNoHyphen) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(MOJANG_SESSION_URL + uuidNoHyphen))
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() / 100 != 2) return null;

            Map<?, ?> root = GSON.fromJson(res.body(), Map.class);
            Object name = root == null ? null : root.get("name");
            if (name instanceof String s && !s.isEmpty()) return s;
        } catch (Exception ignored) {}
        return null;
    }

    private static Set<String> fetchGuildUuids() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(WYNN_GUILD_URL))
                    .header("Accept", "application/json").GET().build();
            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() / 100 != 2) return Collections.emptySet();
            Map<String, Object> root = GSON.fromJson(res.body(), Map.class);
            Object membersObj = root.get("members");
            if (!(membersObj instanceof Map)) return Collections.emptySet();
            Set<String> out = new HashSet<>();
            for (Object rankList : ((Map<?, ?>) membersObj).values()) {
                if (!(rankList instanceof Map)) continue;
                for (Object k : ((Map<?, ?>) rankList).keySet()) {
                    String uuid = normalizeUuid(String.valueOf(k));
                    if (uuid != null) out.add(uuid);
                }
            }
            return out;
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    private static void persist() {
        try (Writer w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(new ArrayList<>(byUuid.values()), ENTRY_LIST, w);
        } catch (IOException ignored) {}
    }

}