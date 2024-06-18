package renektoff.refabricated_necessities.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import renektoff.refabricated_necessities.ModBlockEntities;
import renektoff.refabricated_necessities.blockentities.FeralFlareLanternEntity;

public class FeralFlareLanternBlock extends BlockWithEntity {
    public FeralFlareLanternBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.getDefaultState().with(Properties.FACING, Direction.SOUTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return this.getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return this.getShape(state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.FACING, ctx.getSide());
    }

    private VoxelShape getShape(BlockState state) {
        var direction = state.get(Properties.FACING);

        return switch (direction) {
            case NORTH -> VoxelShapes.cuboid(0.25f, 0.25f, 0.12f, 0.75f, 0.75f, 1.0f);
            case SOUTH -> VoxelShapes.cuboid(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.88f);
            case EAST -> VoxelShapes.cuboid(0.00f, 0.25f, 0.25f, 0.88f, 0.75f, 0.75f);
            case WEST -> VoxelShapes.cuboid(0.12f, 0.25f, 0.25f, 1.00f, 0.75f, 0.75f);
            case UP -> VoxelShapes.cuboid(0.25f, 0.00f, 0.25f, 0.75f, 0.88f, 0.75f);
            case DOWN -> VoxelShapes.cuboid(0.25f, 0.12f, 0.25f, 0.75f, 1.00f, 0.75f);
        };
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FeralFlareLanternEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FERAL_FLARE_LANTERN_ENTITY, (FeralFlareLanternEntity::tick));
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        var entity = world.getBlockEntity(pos);

        if (entity instanceof FeralFlareLanternEntity) {
            ((FeralFlareLanternEntity) entity).removeChildLights();
        }

        super.onBroken(world, pos, state);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        var entity = world.getBlockEntity(pos);

        if (entity instanceof FeralFlareLanternEntity) {
            ((FeralFlareLanternEntity) entity).removeChildLights();
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        var entity = world.getBlockEntity(pos);

        if (entity instanceof FeralFlareLanternEntity) {
            ((FeralFlareLanternEntity) entity).removeChildLights();
        }

        super.onDestroyedByExplosion(world, pos, explosion);
    }
}
