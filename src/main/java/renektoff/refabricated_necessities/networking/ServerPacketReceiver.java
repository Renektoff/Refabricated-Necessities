package renektoff.refabricated_necessities.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.items.MorphTool;
import renektoff.refabricated_necessities.utils.BlockUtils;

public class ServerPacketReceiver {
    public static final Identifier MORPHING_TOOL_MORPH_PACKET_ID = new Identifier("morphing-tool-morphing");
    public static final Identifier MORPHING_TOOL_CLIENT_REMOVE_ITEM_PACKET_ID = new Identifier("morphing-tool-client-remove-item");
    public static final Identifier MORPHING_TOOL_SCROLL_PACKET_ID = new Identifier("morphing-tool-scroll");

    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }

        registerToolMorphingHandler();
        registerToolRemoveItemHandler();
        registerToolScrollHandler();

        initialized = true;
    }

    private static void registerToolMorphingHandler() {
        ServerPlayNetworking.registerGlobalReceiver(MORPHING_TOOL_MORPH_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            var lookedAtPosition = buf.readBlockPos();
            var isOffhandMorph = buf.readBoolean();

            server.execute(() -> {
                var playerInventory = player.getInventory();
                var slotToTransform = isOffhandMorph ? PlayerInventory.OFF_HAND_SLOT : playerInventory.selectedSlot;
                var stackToTransform = playerInventory.getStack(slotToTransform);

                if (!MorphTool.isMorphTool(stackToTransform)) {
                    return;
                }

                var modName = BlockUtils.getNamespace(player.world.getBlockState(lookedAtPosition).getBlock());
                modName = RefabricatedNecessities.CONFIG.MorphingTool.getModNameOrAlias(modName);

                var morphedStack = MorphTool.transformStack(stackToTransform, modName);

                if (morphedStack != stackToTransform && !ItemStack.areItemsEqual(morphedStack, stackToTransform)) {
                    playerInventory.setStack(slotToTransform, morphedStack);
                }
            });
        }));
    }

    private static void registerToolRemoveItemHandler() {
        ServerPlayNetworking.registerGlobalReceiver(MORPHING_TOOL_CLIENT_REMOVE_ITEM_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            var spawnEntity = buf.readBoolean();

            server.execute(() -> {
                var stack = player.getMainHandStack();

                MorphTool.removeItemFromTool(player, stack, spawnEntity);
            });
        }));
    }

    private static void registerToolScrollHandler() {
        ServerPlayNetworking.registerGlobalReceiver(MORPHING_TOOL_SCROLL_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            var direction = buf.readInt();

            server.execute(() -> {

                var stack = player.getMainHandStack();

                if (!MorphTool.isMorphTool(stack)) {
                    return;
                }

                var modName = MorphTool.getAdjacentModName(stack, direction);

                var morphedStack = MorphTool.transformStack(stack, modName);

                var playerInventory = player.getInventory();

                if (morphedStack != stack && !ItemStack.areItemsEqual(morphedStack, stack)) {
                    playerInventory.setStack(playerInventory.selectedSlot, morphedStack);
                }
            });
        }));
    }
}
