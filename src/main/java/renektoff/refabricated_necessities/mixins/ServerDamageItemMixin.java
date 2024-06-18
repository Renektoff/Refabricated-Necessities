package renektoff.refabricated_necessities.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import renektoff.refabricated_necessities.ModItems;
import renektoff.refabricated_necessities.items.MorphTool;

@Mixin(ItemStack.class)
@Environment(EnvType.SERVER)
public class ServerDamageItemMixin {
    @Inject(at = @At(value = "HEAD"), method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)V", cancellable = true)
    public void injectDamageItemStack(int amount, Random random, ServerPlayerEntity serverPlayer, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var damagedItemStack = (ItemStack) (Object) this;

        if (damagedItemStack == null || !damagedItemStack.isDamageable()) {
            return;
        }

        var damageAfterEvent = damagedItemStack.getDamage() + amount;

        if (damageAfterEvent >= damagedItemStack.getMaxDamage()) {
            //Item will break, check if it is MorphTool.
            if (MorphTool.isMorphTool(damagedItemStack) && damagedItemStack.getItem() != ModItems.MORPHING_TOOL) {
                MorphTool.removeItemFromTool(serverPlayer, damagedItemStack, false);

                //Cancel event, prevent the item from being actually broken
                callbackInfoReturnable.setReturnValue(false);

                serverPlayer.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.7f, 0.6f);
            }
        }
    }
}
