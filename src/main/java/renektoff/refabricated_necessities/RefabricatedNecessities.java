package renektoff.refabricated_necessities;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import renektoff.refabricated_necessities.cleanup.LightRemover;
import renektoff.refabricated_necessities.config.ModConfig;
import renektoff.refabricated_necessities.integrations.Wthit.WthitIntegration;
import renektoff.refabricated_necessities.networking.ServerPacketReceiver;

public class RefabricatedNecessities implements ModInitializer {
    public final static String MOD_ID = "refabricated_necessities";
    public static ModConfig CONFIG;

    private static boolean isConfigInitialized;
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModRecipes.init();

        RefabricatedNecessities.initConfig();

        ServerPacketReceiver.init();

        ServerTickEvents.START_SERVER_TICK.register(LightRemover::tick);

        //integrations
        WthitIntegration.tryLoad();

        logInfo("Server setup done!.");
    }

    public static void initConfig() {
        if (isConfigInitialized) {
            return;
        }

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

        var configHolder = AutoConfig.getConfigHolder(ModConfig.class);

        ConfigSerializeEvent.Save<ModConfig> onSave = new ConfigSerializeEvent.Save<ModConfig>() {
            @Override
            public ActionResult onSave(ConfigHolder<ModConfig> configHolder, ModConfig modConfig) {
                modConfig.refreshCalculatedConfigFields();

                return ActionResult.SUCCESS;
            }
        };

        configHolder.registerSaveListener(onSave);

        CONFIG = configHolder.getConfig();
        CONFIG.refreshCalculatedConfigFields();

        isConfigInitialized = true;
    }

    public static void logInfo(String text) {
        LOGGER.info("[Refabricated Necessities] {}", text);
    }
}