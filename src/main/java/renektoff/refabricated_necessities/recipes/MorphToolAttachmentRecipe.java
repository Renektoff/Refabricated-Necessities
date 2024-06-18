package renektoff.refabricated_necessities.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import renektoff.refabricated_necessities.ModItems;
import renektoff.refabricated_necessities.ModRecipes;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.items.MorphTool;
import renektoff.refabricated_necessities.utils.ItemUtils;

public class MorphToolAttachmentRecipe extends SpecialCraftingRecipe {
    private static final Identifier ID = new Identifier(RefabricatedNecessities.MOD_ID, "morph_tool_attachment");

    public MorphToolAttachmentRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        var foundTool = false;
        var foundTarget = false;

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (!stack.isEmpty()) {
                if (RefabricatedNecessities.CONFIG.MorphingTool.isAllowedInMorphTool(stack)) {
                    if (foundTarget) {
                        return false;
                    }

                    foundTarget = true;
                } else if (stack.getItem() == ModItems.MORPHING_TOOL) {
                    if (foundTool) {
                        return false;
                    }

                    foundTool = true;

                } else {
                    return false;
                }
            }
        }

        return foundTool && foundTarget;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack morphTool = ItemStack.EMPTY;
        ItemStack targetItem = ItemStack.EMPTY;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == ModItems.MORPHING_TOOL) {
                    morphTool = stack;
                } else {
                    targetItem = stack;
                }
            }
        }

        var morphToolCopy = morphTool.copy();
        var morphToolCopyNbt = morphToolCopy.getNbt();

        if (morphToolCopyNbt == null) {
            morphToolCopyNbt = new NbtCompound();

            morphToolCopy.setNbt(morphToolCopyNbt);
        }

        if (!morphToolCopyNbt.contains(MorphTool.NBT_MORPH_TOOL_DATA_KEY)) {
            morphToolCopyNbt.put(MorphTool.NBT_MORPH_TOOL_DATA_KEY, new NbtCompound());
        }

        var morphData = morphToolCopyNbt.getCompound(MorphTool.NBT_MORPH_TOOL_DATA_KEY);
        var modName = ItemUtils.getNamespace(targetItem);

        if (morphData.contains(modName)) {
            return ItemStack.EMPTY;
        }

        var modItemNbt = new NbtCompound();

        targetItem.writeNbt(modItemNbt);
        morphData.put(modName, modItemNbt);

        return morphToolCopy;
    }

    @Override
    public boolean fits(int width, int height) {
        return width > 1 && height > 1;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MORPH_TOOL_ATTACHMENT_RECIPE_SERIALIZER;
    }
}
