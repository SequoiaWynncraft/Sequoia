package star.sequoia2.utils.wynn;

import com.wynntils.core.components.Models;
import com.wynntils.core.components.Services;
import com.wynntils.services.hades.HadesUser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HadesUtils {
    public static Set<String> cachedPartyMembers;
    public static Map<String, HadesUser> cachedHadesUsers;

    private static int tickCounter = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            if (tickCounter % 4 == 0) {
                cachedPartyMembers = new HashSet<>(Models.Party.getPartyMembers());
                cachedHadesUsers = Services.Hades.getHadesUsers()
                        .collect(Collectors.toMap(HadesUser::getName, Function.identity()));

            }
        });
    }
}
