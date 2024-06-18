package renektoff.refabricated_necessities.items;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import renektoff.refabricated_necessities.tooltips.MultilineTooltipData;
import renektoff.refabricated_necessities.utils.TextUtils;

import java.util.List;
import java.util.Optional;

public class FeralFlareLanternItem extends BlockItem {

    public FeralFlareLanternItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        var lines = TextUtils.wrapText(Text.translatable("block.refabricated_necessities.feral_flare_lantern.tooltip").getString());

        var multilineData = new MultilineTooltipData(lines, true, null, null);

        return Optional.of(multilineData);
    }
}
