package renektoff.refabricated_necessities.integrations.ModMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.config.ModConfig;

@Environment(EnvType.CLIENT)
public class ModMenuEntrypoint implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        RefabricatedNecessities.initConfig();

        return parent -> AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
}