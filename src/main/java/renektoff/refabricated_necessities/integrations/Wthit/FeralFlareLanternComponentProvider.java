package renektoff.refabricated_necessities.integrations.Wthit;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.text.Text;

public class FeralFlareLanternComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getData().raw();

        if (data.contains(FeralFlareLanternDataProvider.NBT_LIGHTS_COUNT_KEY) && data.contains(FeralFlareLanternDataProvider.NBT_MAX_LIGHTS_KEY)) {
            var result = Text.translatable("integrations.refabricated_necessities.wthit.feral_flare_lantern_format", data.getInt(FeralFlareLanternDataProvider.NBT_LIGHTS_COUNT_KEY), data.getInt(FeralFlareLanternDataProvider.NBT_MAX_LIGHTS_KEY));

            tooltip.addLine(result);
        }
    }
}