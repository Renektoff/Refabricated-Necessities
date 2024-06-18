package renektoff.refabricated_necessities.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class ItemUtils {
    public static String getNamespace(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "";
        }

        return Registry.ITEM.getId(stack.getItem()).getNamespace();
    }

    public static String getPath(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "";
        }

        return Registry.ITEM.getId(stack.getItem()).getPath();
    }

    public static String getNamespaceAndPath(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "";
        }

        return Registry.ITEM.getId(stack.getItem()).toString();
    }
}
