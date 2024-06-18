package renektoff.refabricated_necessities.items;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import renektoff.refabricated_necessities.ModBlockEntities;
import renektoff.refabricated_necessities.blockentities.FeralFlareLanternEntity;
import renektoff.refabricated_necessities.tooltips.MultilineTooltipData;
import renektoff.refabricated_necessities.utils.TextUtils;

import java.util.Optional;

public class FeralFlareTorchItem extends BlockItem {
    private static final String NBT_LANTERN_KEY = "lanternPos";

    public FeralFlareTorchItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static boolean hasBoundLantern(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        var nbtValue = itemStack.getSubNbt(NBT_LANTERN_KEY);

        if (nbtValue == null) {
            return false;
        }

        return true;
    }

    public static FeralFlareLanternEntity getValidBoundLanternEntity(ItemStack itemStack, World world) {
        if (itemStack == null) {
            return null;
        }

        var nbtValue = itemStack.getSubNbt(NBT_LANTERN_KEY);

        if (nbtValue == null) {
            return null;
        }

        var blockPos = NbtHelper.toBlockPos(nbtValue);
        var blockEntity = world.getBlockEntity(blockPos, ModBlockEntities.FERAL_FLARE_LANTERN_ENTITY);

        if (blockEntity.isPresent()) {
            return blockEntity.get();
        }

        return null;
    }

    public void setBoundEntity(ItemStack itemStack, BlockPos blockPos) {
        if (blockPos == null) {
            itemStack.removeSubNbt(NBT_LANTERN_KEY);
            itemStack.removeCustomName();
        } else {
            itemStack.setSubNbt(NBT_LANTERN_KEY, NbtHelper.fromBlockPos(blockPos));
            itemStack.setCustomName(Text.translatable("item.refabricated_necessities.feral_flare_torch.bound"));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);

        var boundLanternEntity = getValidBoundLanternEntity(itemStack, world);

        if (boundLanternEntity != null) {
            if (!this.doesBoundLanternExist(itemStack, world)) {
                this.setBoundEntity(itemStack, null);

                if (world.isClient) {
                    user.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.lantern_not_found"), true);
                }
            } else {
                if (user.isSneaking()) {
                    this.setBoundEntity(itemStack, null);

                    if (world.isClient) {
                        user.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.cleared_bound_lantern"), true);
                        user.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7f, 0.6f);
                    }

                    return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
                } else {
                    return new TypedActionResult<>(ActionResult.FAIL, itemStack);
                }
            }
        }

        //Clears Nbt from item
        this.setBoundEntity(itemStack, null);

        if (world.isClient) {
            user.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.no_lantern"), true);
        }

        return new TypedActionResult<>(ActionResult.FAIL, itemStack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var player = context.getPlayer();

        if (player == null) {
            return ActionResult.FAIL;
        }

        var itemStack = player.getStackInHand(player.getActiveHand());

        if (world.getBlockEntity(context.getBlockPos()) instanceof FeralFlareLanternEntity feralFlareLanternEntity) {
            this.setBoundEntity(itemStack, feralFlareLanternEntity.getPos());

            if (world.isClient) {
                player.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.successfully_bound_lantern"), true);
                player.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 0.7f, 0.6f);
            }

            return ActionResult.success(true);
        }

        var boundLanternEntity = getValidBoundLanternEntity(itemStack, world);

        if (boundLanternEntity == null) {
            setBoundEntity(itemStack, null);

            if (world.isClient) {
                player.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.no_lantern"), true);
            }

            return ActionResult.FAIL;
        }

        var entityPos = boundLanternEntity.getPos();

        if (!world.isPosLoaded(entityPos.getX(), entityPos.getZ())) {
            if (world.isClient) {
                player.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.lantern_not_loaded"), true);
            }

            return ActionResult.FAIL;
        }

        var entity = world.getBlockEntity(boundLanternEntity.getPos());

        if (!(entity instanceof FeralFlareLanternEntity feralFlareLanternEntity)) {
            if (world.isClient) {
                player.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.bound_object_not_lantern"), true);
                player.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.7f, 0.6f);
            }

            this.setBoundEntity(itemStack, null);

            return ActionResult.FAIL;
        }

        if (boundLanternEntity.hasReachedLightLimit()) {
            if (world.isClient) {
                player.sendMessage(Text.translatable("item.refabricated_necessities.feral_flare_torch.messages.reached_light_limit"), true);
            }

            return ActionResult.FAIL;
        }

        var placementBlockPos = new ItemPlacementContext(context).getBlockPos();
        var result = super.useOnBlock(context);

        if (result.isAccepted()) {
            feralFlareLanternEntity.addChildLight(placementBlockPos);

            if (world.isClient) {
                player.playSound(SoundEvents.BLOCK_WOOD_PLACE, 0.7f, 0.6f);
            }
        }

        return result;
    }

    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        var lines = TextUtils.wrapText(Text.translatable("item.refabricated_necessities.feral_flare_torch.tooltip").getString());

        var multilineData = new MultilineTooltipData(lines, true, null, null);

        return Optional.of(multilineData);
    }

    public boolean doesBoundLanternExist(ItemStack itemStack, World world) {
        var boundLanternEntity = getValidBoundLanternEntity(itemStack, world);

        if (boundLanternEntity == null) {
            return false;
        }

        var entityPos = boundLanternEntity.getPos();

        if (!world.isPosLoaded(entityPos.getX(), entityPos.getZ())) {
            return false;
        }

        var entity = world.getBlockEntity(boundLanternEntity.getPos());

        return entity instanceof FeralFlareLanternEntity feralFlareLanternEntity;
    }
}