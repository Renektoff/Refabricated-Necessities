package renektoff.refabricated_necessities.integrations.Wthit;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import renektoff.refabricated_necessities.blockentities.FeralFlareLanternEntity;

public class FeralFlareLanternDataProvider implements IDataProvider<FeralFlareLanternEntity> {
    public static final String NBT_LIGHTS_COUNT_KEY = "lightCount";
    public static final String NBT_MAX_LIGHTS_KEY = "maxLights";

    @Override
    public void appendData(IDataWriter data, IServerAccessor<FeralFlareLanternEntity> accessor, IPluginConfig config) {
        data.raw().putInt(NBT_LIGHTS_COUNT_KEY, accessor.getTarget().getLightCount());
        data.raw().putInt(NBT_MAX_LIGHTS_KEY, accessor.getTarget().getMaxLights());
    }
}
