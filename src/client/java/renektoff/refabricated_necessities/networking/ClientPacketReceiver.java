package renektoff.refabricated_necessities.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import renektoff.refabricated_necessities.events.morphingtool.MorphingToolEventsHandler;
import renektoff.refabricated_necessities.rendering.highlight.BlockHighlighter;

public class ClientPacketReceiver {
    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }

        registerFeralFlareLanternBreakPacketHandler();
        registerMorphingToolItemRemovePacketHandler();

        initialized = true;
    }

    private static void registerFeralFlareLanternBreakPacketHandler() {
        ClientPlayNetworking.registerGlobalReceiver(ServerPacketSender.FERAL_FLARE_LANTERN_BROKEN_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                BlockHighlighter.updateHighlightedBlock(client);
            });
        });
    }

    private static void registerMorphingToolItemRemovePacketHandler() {
        ClientPlayNetworking.registerGlobalReceiver(ServerPacketSender.MORPHING_TOOL_SERVER_ITEM_REMOVED_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                MorphingToolEventsHandler.autoTransform = true;
            });
        });
    }
}
