package renektoff.refabricated_necessities;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import renektoff.refabricated_necessities.items.FeralFlareTorchItem;

public class ModModelPredicates {
    public static void init() {
        ModelPredicateProviderRegistry.register(ModItems.FERAL_FLARE_TORCH, new Identifier("bound"), (itemStack, clientWorld, livingEntity, number) -> {
            if (livingEntity == null) {
                return 0.0F;
            }

            if (itemStack.getItem() == ModItems.FERAL_FLARE_TORCH) {
                return FeralFlareTorchItem.hasBoundLantern(itemStack) ? 1.0F : 0.0F;
            }

            return 0.0F;
        });
    }
}
