package renektoff.refabricated_necessities.events.morphingtool;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MorphingToolEvents {
    public static final Event<MorphingToolMorph> TOOL_MORPH = EventFactory.createArrayBacked(MorphingToolMorph.class,
            callbacks -> (world, lookTargetBlockPos, hand, morphedStack) -> {
                for (var callback : callbacks) {
                    callback.onToolMorph(world, lookTargetBlockPos, hand, morphedStack);
                }
            });

    @FunctionalInterface
    public interface MorphingToolMorph {
        void onToolMorph(MinecraftClient client, BlockPos lookTargetBlockPos, Hand hand, ItemStack morphedStack);
    }
}
