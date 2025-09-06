package star.sequoia2.client.services.wynn.guild;

import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.Service;
import star.sequoia2.http.HttpClients;
import star.sequoia2.utils.URLUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class GuildService extends Service {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/guild/%s";

    public GuildService() {
        super(List.of());
    }

    public CompletableFuture<GuildResponse> getGuild(String guildName) {
        String baseUrl = String.format(BASE_URL, URLUtils.sanitize(guildName));
        String prefixUrl = String.format(BASE_URL, "prefix/" + URLUtils.sanitize(guildName));

        CompletableFuture<GuildResponse> normalResponse =
                HttpClients.WYNNCRAFT_API.getJsonAsync(baseUrl, GuildResponse.class);
        return normalResponse.thenCompose(response -> {
            if (response != null) {
                SeqClient.debug("Fetched guild data: " + response);
                return CompletableFuture.completedFuture(response);
            } else {
                return HttpClients.WYNNCRAFT_API
                        .getJsonAsync(prefixUrl, GuildResponse.class)
                        .thenApply(prefixResponse -> {
                            if (prefixResponse != null) {
                                SeqClient.debug("Fetched guild data with prefix: " + prefixResponse);
                                return prefixResponse;
                            } else {
                                SeqClient.error("Failed to fetch guild data");
                                return null;
                            }
                        });
            }
        });
    }
}