package renektoff.refabricated_necessities.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import renektoff.refabricated_necessities.RefabricatedNecessities;

import java.util.ArrayList;

public class RefabricatedNecessitiesState extends PersistentState {
    private final static String NBT_REMOVED_BLOCK_POSITIONS_KEY = "removedBlockPositions";

    public ArrayList<BlockPos> removedLightPositions = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        if (!removedLightPositions.isEmpty()) {
            var list = new NbtList();

            for (var blockPos : removedLightPositions) {
                list.add(NbtHelper.fromBlockPos(blockPos));
            }

            nbt.put(NBT_REMOVED_BLOCK_POSITIONS_KEY, list);
        }

        return nbt;
    }

    public static RefabricatedNecessitiesState createFromNbt(NbtCompound tag) {
        var state = new RefabricatedNecessitiesState();

        if (tag.contains(NBT_REMOVED_BLOCK_POSITIONS_KEY)) {
            var nbtList = tag.getList(NBT_REMOVED_BLOCK_POSITIONS_KEY, NbtElement.COMPOUND_TYPE);

            for (var nbtElement : nbtList) {
                state.removedLightPositions.add(NbtHelper.toBlockPos((NbtCompound) nbtElement));
            }
        }

        return state;
    }

    public static RefabricatedNecessitiesState getServerState(ServerWorld world) {
        var stateManager = world.getPersistentStateManager();

        if (stateManager != null) {
            var state = stateManager.getOrCreate(
                    RefabricatedNecessitiesState::createFromNbt,
                    RefabricatedNecessitiesState::new,
                    RefabricatedNecessities.MOD_ID);

            state.markDirty();

            return state;
        }

        return null;
    }
}
