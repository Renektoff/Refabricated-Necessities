package renektoff.refabricated_necessities.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class MorphToolAttachmentRecipeSerializer implements RecipeSerializer<MorphToolAttachmentRecipe> {
    @Override
    public MorphToolAttachmentRecipe read(Identifier id, JsonObject json) {
        return new MorphToolAttachmentRecipe(id);
    }

    @Override
    public MorphToolAttachmentRecipe read(Identifier id, PacketByteBuf buf) {
        return new MorphToolAttachmentRecipe(id);
    }

    @Override
    public void write(PacketByteBuf buf, MorphToolAttachmentRecipe recipe) {
    }
}
