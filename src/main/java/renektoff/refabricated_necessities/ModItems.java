package renektoff.refabricated_necessities;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import renektoff.refabricated_necessities.items.*;

public class ModItems {
    public static FeralFlareLanternItem FERAL_FLARE_LANTERN;
    public static FrozenPearlItem FROZEN_PEARL;
    public static FeralFlareTorchItem FERAL_FLARE_TORCH;
    public static MorphTool MORPHING_TOOL;
    public static SootyStick SOOTY_STICK;

    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }

        FERAL_FLARE_LANTERN = Registry.register(
                Registry.ITEM,
                new Identifier(RefabricatedNecessities.MOD_ID, "feral_flare_lantern"),
                new FeralFlareLanternItem(ModBlocks.FERAL_FLARE_LANTERN_BLOCK, new FabricItemSettings()
                        .group(ItemGroup.MISC))
        );

        FROZEN_PEARL = Registry.register(
                Registry.ITEM,
                new Identifier(RefabricatedNecessities.MOD_ID, "frozen_pearl"),
                new FrozenPearlItem(new FabricItemSettings()
                        .group(ItemGroup.MISC))
        );

        FERAL_FLARE_TORCH = Registry.register(
                Registry.ITEM,
                new Identifier(RefabricatedNecessities.MOD_ID, "feral_flare_torch"),
                new FeralFlareTorchItem(ModBlocks.INVISIBLE_LIGHT_BLOCK, new FabricItemSettings()
                        .group(ItemGroup.MISC))
        );

        MORPHING_TOOL = Registry.register(
                Registry.ITEM,
                new Identifier(RefabricatedNecessities.MOD_ID, "morph_tool"),
                new MorphTool(new FabricItemSettings()
                        .maxCount(1)
                        .group(ItemGroup.TOOLS))
        );

        SOOTY_STICK = Registry.register(
                Registry.ITEM,
                new Identifier(RefabricatedNecessities.MOD_ID, "sooty_stick"),
                new SootyStick(new FabricItemSettings()
                        .group(ItemGroup.MATERIALS))
        );

        initialized = true;
    }
}
