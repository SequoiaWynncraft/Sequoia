package star.sequoia2.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static star.sequoia2.client.NectarClient.mc;

public class SimpleProfileFetcher {
    private static final ConcurrentHashMap<UUID,   CompletableFuture<Optional<GameProfile>>> BY_ID   = new ConcurrentHashMap<>();

    ApiServices services;

    public SimpleProfileFetcher() {
        if (mc.authenticationService != null) {
            services = ApiServices.create(mc.authenticationService, mc.runDirectory);
        }
    }

    public CompletableFuture<Optional<GameProfile>> fetchByUUID(UUID id) {
        if (mc.getSessionService() == null || id == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return BY_ID.computeIfAbsent(id, uuid ->
            CompletableFuture.supplyAsync(() -> {
                ProfileResult res = services.sessionService().fetchProfile(uuid, true);
                return Optional.ofNullable(res).map(ProfileResult::profile);
            }, Util.getMainWorkerExecutor())
        );
    }
}
