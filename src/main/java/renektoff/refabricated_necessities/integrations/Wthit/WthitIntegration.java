package renektoff.refabricated_necessities.integrations.Wthit;

import net.fabricmc.loader.api.FabricLoader;
import renektoff.refabricated_necessities.RefabricatedNecessities;

public class WthitIntegration {
    public static void tryLoad() {
        if (FabricLoader.getInstance().isModLoaded("wthit")) {
            RefabricatedNecessities.logInfo("Wthit mod found. Loading integration.");

            new WthitPlugin(); //Ensure reference so it loads.
        }
    }
}
