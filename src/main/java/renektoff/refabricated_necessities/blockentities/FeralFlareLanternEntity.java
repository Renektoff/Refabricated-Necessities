package renektoff.refabricated_necessities.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import renektoff.refabricated_necessities.ModBlockEntities;
import renektoff.refabricated_necessities.ModBlocks;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.cleanup.LightRemover;
import renektoff.refabricated_necessities.networking.ServerPacketSender;
import renektoff.refabricated_necessities.utils.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class FeralFlareLanternEntity extends BlockEntity {
    private final static String NBT_LIGHTS_KEY = "lights";
    private final static String NBT_TAG_TICKS_KEY = "ticks";

    private int ticks;
    private final List<BlockPos> childLights = new ArrayList<>();

    //Whenever we find a surface, we lock the Y for the next N checks to increase our odds of hitting more 'valuable' blocks
    public boolean isYLocked = false;
    public int lockedYLevel = 999;
    public int currentLockedYChecks = 0;

    public FeralFlareLanternEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERAL_FLARE_LANTERN_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        List<Integer> childLightsEncoded = new ArrayList<>(this.childLights.size());
        for (BlockPos child : this.childLights) {
            childLightsEncoded.add(encodePosition(this.pos, child));
        }

        nbt.put(NBT_LIGHTS_KEY, new NbtIntArray(childLightsEncoded));
        nbt.putInt(NBT_TAG_TICKS_KEY, this.ticks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.childLights.clear();

        if (nbt.contains(NBT_LIGHTS_KEY)) {
            var encodedLights = nbt.getIntArray(NBT_LIGHTS_KEY);

            if (encodedLights != null && encodedLights.length > 0) {
                var origin = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));

                for (var encodedLight : encodedLights) {
                    this.childLights.add(decodePosition(origin, encodedLight));
                }
            }
        }

        this.ticks = nbt.getInt(NBT_TAG_TICKS_KEY);

        super.readNbt(nbt);
    }

    public int getLightCount() {
        return this.childLights.size();
    }

    public int getMaxLights() {
        return RefabricatedNecessities.CONFIG.FeralFlareLantern.lightsHardCap;
    }

    public void addChildLight(BlockPos pos) {
        this.childLights.add(pos);
    }

    public void removeChildLights() {
        if (this.world == null || this.world.isClient) {
            return;
        }

        LightRemover.schedule((ServerWorld) this.world, this.childLights);

        for (var player : ((ServerWorld) this.world).getPlayers()) {
            ServerPacketSender.sendBrokenLanternPacket(player);
        }
    }

    public boolean hasReachedLightLimit() {
        return this.childLights.size() >= RefabricatedNecessities.CONFIG.FeralFlareLantern.lightsHardCap;
    }

    public static void tick(World world, BlockPos ignoredPos, BlockState ignoredState, FeralFlareLanternEntity be) {
        if (world.isClient || ++be.ticks % RefabricatedNecessities.CONFIG.FeralFlareLantern.tickRate != 0) {
            return;
        }
        be.ticks = 0;

        if (be.hasReachedLightLimit()) {
            return;
        }

        var radius = RefabricatedNecessities.CONFIG.FeralFlareLantern.lightRadius;
        var diameter = radius * 2;

        if (be.isYLocked && be.currentLockedYChecks >= diameter) {
            be.isYLocked = false;
            be.currentLockedYChecks = 0;
        }

        var x = (radius - world.random.nextInt(diameter)) + be.pos.getX();
        var y = be.isYLocked ? be.lockedYLevel : (radius - world.random.nextInt(diameter)) + be.pos.getY();
        var z = (radius - world.random.nextInt(diameter)) + be.pos.getZ();

        var targetPos = new BlockPos(x, y, z);

        if (!world.isPosLoaded(targetPos.getX(), targetPos.getZ())) {
            return;
        }

        var worldHeightCap = world.getHeight();
        if (targetPos.getY() > worldHeightCap) {
            targetPos = new BlockPos(targetPos.getX(), worldHeightCap - 1, targetPos.getZ());
        }

        //Make sure we're only placing down blocks on blocks that have at least 1 solid neighbour
        if (world.isAir(targetPos) && BlockUtils.hasSolidNeighbourBlock(targetPos, world)) {
            if (!be.isYLocked) {
                //Check if a good candidate. Considered surface if has a solid block under and one air above for simplicity
                var belowPos = targetPos.add(0, -1, 0);

                if (!world.isAir(belowPos)) {
                    var abovePos = targetPos.add(0, 1, 0);

                    if (world.isAir(abovePos)) {
                        be.isYLocked = true;
                        be.lockedYLevel = targetPos.getY();
                        be.currentLockedYChecks = 0;
                    }
                }
            }

            if (world.getLuminance(targetPos) < RefabricatedNecessities.CONFIG.FeralFlareLantern.minLightLevel && world.canSetBlock(targetPos)) {
                world.setBlockState(targetPos, ModBlocks.INVISIBLE_LIGHT_BLOCK.getDefaultState());

                be.addChildLight(targetPos);
            }

            if (be.isYLocked) {
                be.currentLockedYChecks++;
            }
        }
    }

    private static int encodePosition(BlockPos origin, BlockPos target) {
        int x = target.getX() - origin.getX();
        int y = target.getY() - origin.getY();
        int z = target.getZ() - origin.getZ();

        return ((x & 0xFF) << 16) + ((y & 0xFF) << 8) + (z & 0xFF);
    }

    private static BlockPos decodePosition(BlockPos origin, int pos) {
        int x = (byte) ((pos >> 16) & 0xFF);
        int y = (byte) ((pos >> 8) & 0xFF);
        int z = (byte) (pos & 0xFF);

        return origin.add(x, y, z);
    }
}