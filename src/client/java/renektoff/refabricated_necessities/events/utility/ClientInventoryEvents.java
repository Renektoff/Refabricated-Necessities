package renektoff.refabricated_necessities.events.utility;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClientInventoryEvents {
    public static final Event<HeldItemChange> HELD_ITEM_CHANGE = EventFactory.createArrayBacked(HeldItemChange.class,
            callbacks -> (world, previousItem, newItem, hand) -> {
                for (var callback : callbacks) {
                    callback.onHeldItemChange(world, previousItem, newItem, hand);
                }
            });

    public static final Event<ItemDrop> ITEM_DROP = EventFactory.createArrayBacked(ItemDrop.class,
            callbacks -> (client, itemStack, dropEntireStack, callbackInfoReturnable) -> {
                for (var callback : callbacks) {
                    if (!callbackInfoReturnable.isCancelled()) {
                        callback.onItemDrop(client, itemStack, dropEntireStack, callbackInfoReturnable);
                    }
                }
            });

    public static final Event<HotbarScroll> HOTBAR_SCROLL = EventFactory.createArrayBacked(HotbarScroll.class,
            callbacks -> (client, itemStack, scrollDirection, callbackInfo) -> {
                for (var callback : callbacks) {
                    if (!callbackInfo.isCancelled()) {
                        callback.onHotbarScroll(client, itemStack, scrollDirection, callbackInfo);
                    }
                }
            });

    @FunctionalInterface
    public interface HeldItemChange {
        void onHeldItemChange(MinecraftClient client, ItemStack previousItem, ItemStack newItem, Hand hand);
    }

    @FunctionalInterface
    public interface ItemDrop {
        void onItemDrop(MinecraftClient client, ItemStack itemStack, boolean dropEntireStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable);
    }

    @FunctionalInterface
    public interface HotbarScroll{
        void onHotbarScroll(MinecraftClient client, ItemStack currentItem, int scrollDirection, CallbackInfo callbackInfo);
    }
}
