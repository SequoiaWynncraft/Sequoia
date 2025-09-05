package star.sequoia2.http;

import com.mojang.logging.LogUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class Http {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    public static CompletableFuture<JSONObject> fetchPlayerFullStats(String usernameOrUuid) {
        final String id = URLEncoder.encode(usernameOrUuid, StandardCharsets.UTF_8);
        final URI uri = URI.create("https://api.wynncraft.com/v3/player/" + id + "?fullResult");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .header("User-Agent", "SeqClient/1.0 (+https://youtube.com/@theoplegends)")
                .GET()
                .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int code = response.statusCode();
                    if (code < 200 || code >= 300) {
                        throw new CompletionException(new IOException("Wynncraft API returned " + code + ": " + response.body()));
                    }
                    try {
                        Object parsed = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(response.body());
                        if (parsed instanceof JSONObject obj) return obj;
                        // Shouldn’t happen for ?fullResult, but guard anyway:
                        throw new CompletionException(new IOException("Unexpected JSON type: " + parsed.getClass()));
                    } catch (Exception e) {
                        throw new CompletionException(new IOException("Failed to parse Wynncraft response", e));
                    }
                });
    }
}