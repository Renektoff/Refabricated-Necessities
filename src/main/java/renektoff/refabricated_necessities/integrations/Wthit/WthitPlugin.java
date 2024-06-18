package renektoff.refabricated_necessities.integrations.Wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import renektoff.refabricated_necessities.blockentities.FeralFlareLanternEntity;

public class WthitPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addBlockData(new FeralFlareLanternDataProvider(), FeralFlareLanternEntity.class);
        registrar.addComponent(new FeralFlareLanternComponentProvider(), TooltipPosition.BODY, FeralFlareLanternEntity.class);
    }
}
