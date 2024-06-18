package renektoff.refabricated_necessities;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import renektoff.refabricated_necessities.recipes.MorphToolAttachmentRecipe;

public class ModRecipes {
    private static boolean initialized;

    public static SpecialRecipeSerializer<MorphToolAttachmentRecipe> MORPH_TOOL_ATTACHMENT_RECIPE_SERIALIZER;
    public static RecipeType<MorphToolAttachmentRecipe> MORPH_TOOL_ATTACHMENT_RECIPE_TYPE;

    public static void init() {
        if (initialized) {
            return;
        }

        MORPH_TOOL_ATTACHMENT_RECIPE_SERIALIZER = RecipeSerializer.register(new Identifier(RefabricatedNecessities.MOD_ID, "add_morph_tool_attachment").toString(), new SpecialRecipeSerializer<>(MorphToolAttachmentRecipe::new));

        MORPH_TOOL_ATTACHMENT_RECIPE_TYPE = Registry.register(
                Registry.RECIPE_TYPE,
                new Identifier(RefabricatedNecessities.MOD_ID, "morph_tool_attachment_recipe_type"),
                new RecipeType<MorphToolAttachmentRecipe>() {
                    @Override
                    public String toString() {
                        return new Identifier(RefabricatedNecessities.MOD_ID, "morph_tool_attachment_recipe_type").toString();
                    }
                });

        initialized = true;
    }
}
