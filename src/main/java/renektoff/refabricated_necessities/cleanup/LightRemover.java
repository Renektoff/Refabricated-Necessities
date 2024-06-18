package renektoff.refabricated_necessities.cleanup;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import renektoff.refabricated_necessities.ModBlocks;
import renektoff.refabricated_necessities.state.RefabricatedNecessitiesState;

import java.util.List;

public class LightRemover {
    //How many blocks to clean up per tick
    private static final int TICK_MAX_BATCH_SIZE = 5;

    public static void schedule(ServerWorld world, List<BlockPos> positions) {
        var state = RefabricatedNecessitiesState.getServerState(world);

        if (state == null) {
            return;
        }

        state.removedLightPositions.addAll(positions);
    }

    public static void tick(MinecraftServer minecraftServer) {
        for (var world : minecraftServer.getWorlds()) {
            var state = RefabricatedNecessitiesState.getServerState(world);

            if (state == null) {
                return;
            }

            var removedWorldBlockPositions = state.removedLightPositions;

            if (removedWorldBlockPositions.isEmpty()) {
                return;
            }

            var upperBound = Math.min(TICK_MAX_BATCH_SIZE, removedWorldBlockPositions.size());
            var blockPosBatch = removedWorldBlockPositions.subList(0, upperBound);

            for (var pos : blockPosBatch) {
                var blockState = world.getBlockState(pos);

                if (blockState != null) {
                    if (blockState.getBlock() == ModBlocks.INVISIBLE_LIGHT_BLOCK) {
                        world.removeBlock(pos, false);
                    }
                }
            }

            blockPosBatch.clear();
        }
    }
}
