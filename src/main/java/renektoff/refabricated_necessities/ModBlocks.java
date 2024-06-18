package renektoff.refabricated_necessities;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import renektoff.refabricated_necessities.blocks.FeralFlareLanternBlock;

public class ModBlocks {
    public static FeralFlareLanternBlock FERAL_FLARE_LANTERN_BLOCK;
    public static AirBlock INVISIBLE_LIGHT_BLOCK;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }

        FERAL_FLARE_LANTERN_BLOCK = Registry.register(
                Registry.BLOCK,
                new Identifier(RefabricatedNecessities.MOD_ID, "feral_flare_lantern"),
                new FeralFlareLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN)
                        .requiresTool())
        );

        INVISIBLE_LIGHT_BLOCK = Registry.register(
                Registry.BLOCK,
                new Identifier(RefabricatedNecessities.MOD_ID, "invisible_light"),
                new AirBlock(FabricBlockSettings.copyOf(Blocks.AIR)
                        .luminance(15)
                        .noCollision()
                        .air())
        );

        initialized = true;
    }
}