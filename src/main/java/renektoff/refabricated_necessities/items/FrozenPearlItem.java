package renektoff.refabricated_necessities.items;

import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import renektoff.refabricated_necessities.ModBlocks;
import renektoff.refabricated_necessities.tooltips.MultilineTooltipData;
import renektoff.refabricated_necessities.utils.TextUtils;

import java.util.Optional;

public class FrozenPearlItem extends Item {
    public FrozenPearlItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var checkPos = new BlockPos.Mutable(0, 0, 0);
        var pos = user.getBlockPos();
        var itemStack = user.getStackInHand(hand);
        world.playSound(user, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.7f, 0.6f);
        for (int x = -15; x <= 15; x++) {
            for (int y = -15; y <= 15; y++) {
                for (int z = -15; z <= 15; z++) {
                    checkPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);

                    var block = world.getBlockState(checkPos).getBlock();

                    if (block == ModBlocks.INVISIBLE_LIGHT_BLOCK) {
                        world.removeBlock(checkPos, false);

                        if (itemStack.isDamageable())
                            itemStack.damage(1, user, (entity) -> entity.sendToolBreakStatus(hand));
                        if (itemStack.isEmpty())
                            return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
                    }
                }
            }
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        var lines = TextUtils.wrapText(Text.translatable("item.refabricated_necessities.frozen_pearl.tooltip").getString());

        var multilineData = new MultilineTooltipData(lines, true, null, null);

        return Optional.of(multilineData);
    }
}
