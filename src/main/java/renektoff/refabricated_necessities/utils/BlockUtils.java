package renektoff.refabricated_necessities.utils;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BlockUtils {
    public static String getNamespace(World world, BlockPos blockPos) {
        var blockState = world.getBlockState(blockPos);

        return getNamespace(blockState.getBlock());
    }

    public static String getNamespace(Block block) {
        if (block == null) {
            return "";
        }

        return Registry.BLOCK.getId(block).getNamespace();
    }

    public static boolean hasSolidNeighbourBlock(BlockPos pos, World world) {
        if (!world.isAir(pos.add(-1, 0, 0))) {
            return true;
        }

        if (!world.isAir(pos.add(1, 0, 0))) {
            return true;
        }

        if (!world.isAir(pos.add(0, 0, 1))) {
            return true;
        }

        if (!world.isAir(pos.add(0, 0, -1))) {
            return true;
        }

        if (!world.isAir(pos.add(0, 1, 0))) {
            return true;
        }

        if (!world.isAir(pos.add(0, -1, 0))) {
            return true;
        }

        return false;
    }
}
