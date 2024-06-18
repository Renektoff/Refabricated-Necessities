package renektoff.refabricated_necessities;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import renektoff.refabricated_necessities.events.morphingtool.MorphingToolEventsHandler;
import renektoff.refabricated_necessities.events.utility.ClientInventoryEventsHandler;
import renektoff.refabricated_necessities.integrations.ModMenu.ModMenuIntegration;
import renektoff.refabricated_necessities.integrations.Wthit.WthitIntegration;
import renektoff.refabricated_necessities.networking.ClientPacketReceiver;
import renektoff.refabricated_necessities.rendering.highlight.BlockHighlighter;

@Environment(EnvType.CLIENT)
public class RefabricatedNecessitiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModRecipes.init();

        ModModelPredicates.init();

        RefabricatedNecessities.initConfig();

        WorldRenderEvents.LAST.register(BlockHighlighter::onRender);

        ClientInventoryEventsHandler.init();
        MorphingToolEventsHandler.init();

        ClientPacketReceiver.init();

        //Integrations
        ModMenuIntegration.tryLoad();
        WthitIntegration.tryLoad();

        RefabricatedNecessities.logInfo("Client setup done!.");
    }
}