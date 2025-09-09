package star.sequoia2;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Seq implements ModInitializer {

    @Getter
    private static boolean hasMCEF = false;

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("mcef")) {
            hasMCEF = true;
        }
    }
}
