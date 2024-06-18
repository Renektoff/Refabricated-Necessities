package renektoff.refabricated_necessities.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import renektoff.refabricated_necessities.events.utility.ClientInventoryEvents;

@Mixin(ClientPlayerEntity.class)
public class ClientDropItemMixin {
    @Inject(at = @At(value = "HEAD"), method = "dropSelectedItem(Z)Z", cancellable = true)
    public void injectDropEvent(boolean entireStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var player = MinecraftClient.getInstance().player;

        if (player != null) {
            var stack = player.getMainHandStack();

            ClientInventoryEvents.ITEM_DROP.invoker().onItemDrop(MinecraftClient.getInstance(), stack, entireStack, callbackInfoReturnable);
        }
    }
}
