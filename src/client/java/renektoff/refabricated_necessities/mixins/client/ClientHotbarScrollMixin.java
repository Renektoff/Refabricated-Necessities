package renektoff.refabricated_necessities.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import renektoff.refabricated_necessities.events.utility.ClientInventoryEvents;

@Mixin(PlayerInventory.class)
public class ClientHotbarScrollMixin {
    @Inject(at = @At(value = "HEAD"), method = "scrollInHotbar(D)V", cancellable = true)
    public void injectScrollInHotbar(double scrollAmount, CallbackInfo callbackInfo) {
        var player = MinecraftClient.getInstance().player;

        if (player != null) {
            var stack = player.getMainHandStack();
            var scrollDirection = (int)Math.signum(scrollAmount);

            ClientInventoryEvents.HOTBAR_SCROLL.invoker().onHotbarScroll(MinecraftClient.getInstance(), stack, scrollDirection, callbackInfo);
        }
    }
}
