package renektoff.refabricated_necessities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import renektoff.refabricated_necessities.blockentities.FeralFlareLanternEntity;

public class ModBlockEntities {
    public static BlockEntityType<FeralFlareLanternEntity> FERAL_FLARE_LANTERN_ENTITY;

    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }

        FERAL_FLARE_LANTERN_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(RefabricatedNecessities.MOD_ID, "feral_flare_lantern"),
                FabricBlockEntityTypeBuilder.create(FeralFlareLanternEntity::new, ModBlocks.FERAL_FLARE_LANTERN_BLOCK).build()
        );

        initialized = true;
    }
}
