package star.sequoia2.client.services.wynn.player;

import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.Service;
import star.sequoia2.client.types.Services;
import star.sequoia2.http.HttpClients;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerService extends Service {
    private static final String BASE_URL = "https://api.wynncraft.com/v3/player/%s";
    private static final String FULL_RESULT_URL = BASE_URL + "?fullResult";

    public PlayerService() {
        super(List.of());
    }

    public CompletableFuture<PlayerResponse> getPlayer(String username) {
        String url = String.format(BASE_URL, username);
        return HttpClients.WYNNCRAFT_API.getJsonAsync(url, PlayerResponse.class).thenCompose(playerResponse -> {
            if (playerResponse == null) {
                UUID uuid = Services.Mojang.getUUID(username).join();
                return getPlayer(uuid.toString());
            }
            SeqClient.debug("Fetched player data for username: " + username);
            return CompletableFuture.completedFuture(playerResponse);
        });
    }

    public CompletableFuture<PlayerResponse> getPlayerFullResult(String username) {
        String url = String.format(FULL_RESULT_URL, username);
        return HttpClients.WYNNCRAFT_API.getJsonAsync(url, PlayerResponse.class).thenCompose(playerResponse -> {
            if (playerResponse == null) {
                UUID uuid = Services.Mojang.getUUID(username).join();
                return getPlayerFullResult(uuid.toString());
            }
            SeqClient.debug("Fetched full player data for username: " + username);
            return CompletableFuture.completedFuture(playerResponse);
        });
    }

}
