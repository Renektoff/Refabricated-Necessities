package renektoff.refabricated_necessities.integrations.ModMenu;

import net.fabricmc.loader.api.FabricLoader;
import renektoff.refabricated_necessities.RefabricatedNecessities;

public class ModMenuIntegration {
    public static void tryLoad() {
        if (FabricLoader.getInstance().isModLoaded("mod_menu")) {
            RefabricatedNecessities.logInfo("ModMenu mod found. Loading integration.");

            new ModMenuEntrypoint(); //Reference to ensure load
        }
    }
}
