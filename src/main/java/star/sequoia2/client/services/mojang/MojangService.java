package star.sequoia2.client.services.mojang;

import star.sequoia2.client.SeqClient;
import star.sequoia2.http.HttpClients;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class MojangService extends Service {
    private static final String USERS_PROFILES_MINECRAFT_BASE_URL =
            "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final Pattern UNDASHED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public MojangService() {
        super(List.of());
    }

    public CompletableFuture<UUID> getUUID(String username) {
        String url = String.format(USERS_PROFILES_MINECRAFT_BASE_URL, username);
        return HttpClients.MOJANG_API
                .getJsonAsync(url, MojangUsersProfilesMinecraftResponse.class)
                .thenApply(response -> {
                    if (response != null) {
                        try {
                            return UUID.fromString(UNDASHED_UUID_PATTERN
                                    .matcher(response.getId())
                                    .replaceFirst("$1-$2-$3-$4-$5"));
                        } catch (Exception exception) {
                            SeqClient.error("Failed to parse UUID from player data", exception);
                            return null;
                        }
                    } else {
                        SeqClient.error("Failed to fetch Mojang player data");
                        return null;
                    }
                });
    }
}