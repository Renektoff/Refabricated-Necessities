package renektoff.refabricated_necessities.events.morphingtool;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.items.MorphTool;
import renektoff.refabricated_necessities.networking.ClientPacketReceiver;
import renektoff.refabricated_necessities.networking.ClientPacketSender;
import renektoff.refabricated_necessities.utils.BlockUtils;

public class MorphingToolEventsHandler {
    private static boolean initialized;

    public static boolean autoTransform = true;

    public static void init() {
        if (initialized) {
            return;
        }

        ClientTickEvents.START_CLIENT_TICK.register(MorphingToolEventsHandler::onClientTick);
        MorphingToolEvents.TOOL_MORPH.register(MorphingToolEventsHandler::onToolMorph);

        initialized = true;
    }

    private static void onClientTick(MinecraftClient minecraftClient) {
        if (minecraftClient.world != null && minecraftClient.player != null && autoTransform) {
            var mainHandItemStack = minecraftClient.player.getStackInHand(Hand.MAIN_HAND);
            var offHandItemStack = minecraftClient.player.getStackInHand(Hand.OFF_HAND);

            var isMainMorph = MorphTool.isMorphTool(mainHandItemStack);
            var isOffMorph = MorphTool.isMorphTool(offHandItemStack);

            if (isMainMorph && !RefabricatedNecessities.CONFIG.MorphingTool.morphInOffhand) {
                processBlockLook(minecraftClient, getLookedAtBlock(minecraftClient), mainHandItemStack, Hand.MAIN_HAND);
            } else if (isOffMorph && RefabricatedNecessities.CONFIG.MorphingTool.morphInOffhand) {
                processBlockLook(minecraftClient, getLookedAtBlock(minecraftClient), offHandItemStack, Hand.OFF_HAND);
            }
        }
    }

    private static void processBlockLook(MinecraftClient minecraftClient, BlockPos blockTarget, ItemStack handStack, Hand hand) {
        if (blockTarget != null) {
            var modName = BlockUtils.getNamespace(minecraftClient.world, blockTarget);

            var morphedStack = MorphTool.transformStack(handStack, modName);

            if (morphedStack != handStack && !ItemStack.areItemsEqual(morphedStack, handStack)) {
                MorphingToolEvents.TOOL_MORPH.invoker().onToolMorph(minecraftClient, blockTarget, hand, morphedStack);
            }
        }
    }

    private static BlockPos getLookedAtBlock(MinecraftClient minecraftClient) {
        var hit = minecraftClient.crosshairTarget;

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            if (minecraftClient.world != null) {
                return ((BlockHitResult) hit).getBlockPos();
            }
        }

        return null;
    }

    private static void onToolMorph(MinecraftClient minecraftClient, BlockPos blockLookAtTarget, Hand hand, ItemStack morphedStack) {
        ClientPacketSender.sendToolMorphPacket(blockLookAtTarget, hand);
    }
}
