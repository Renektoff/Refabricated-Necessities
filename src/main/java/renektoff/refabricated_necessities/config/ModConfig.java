package renektoff.refabricated_necessities.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.item.ItemStack;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.HashMap;

@Config(name = RefabricatedNecessities.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public FeralFlareLanternConfig FeralFlareLantern = new FeralFlareLanternConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public MorphingToolConfig MorphingTool = new MorphingToolConfig();

    public void refreshCalculatedConfigFields() {
        this.MorphingTool.refreshCalculatedConfigFields();
    }

    public static class FeralFlareLanternConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 2, max = 20)
        public int tickRate = 5;

        @ConfigEntry.Gui.Tooltip
        public int lightsHardCap = 255;

        @ConfigEntry.Gui.Tooltip
        public int lightRadius = 16;

        @ConfigEntry.Gui.Tooltip
        public int minLightLevel = 10;
    }

    public static class MorphingToolConfig {
        @ConfigEntry.Gui.Excluded
        public transient final String MINECRAFT_MOD = "minecraft";

        public boolean morphInOffhand = false;

        @ConfigEntry.Gui.Tooltip
        public ArrayList<String> denylistMods = this.getDefaultModDenyList();

        @ConfigEntry.Gui.Tooltip
        public ArrayList<String> allowlistItems = this.getDefaultAllowListItems();

        @ConfigEntry.Gui.Tooltip
        public ArrayList<String> allowlistNames = this.getDefaultAllowListNames();

        @ConfigEntry.Gui.Tooltip(count = 2)
        public ArrayList<String> modAliases = this.getDefaultModAliases();

        @ConfigEntry.Gui.Excluded
        public transient HashMap<String, String> modAliasesMap;

        public String getModNameOrAlias(String modName) {
            if (modName == null || modName.isEmpty()) {
                return MINECRAFT_MOD;
            }

            return this.modAliasesMap.getOrDefault(modName, modName);
        }

        public boolean isAllowedInMorphTool(ItemStack itemStack) {
            if (itemStack == null || itemStack.isEmpty()) {
                return false;
            }

            var modName = ItemUtils.getNamespace(itemStack);

            if (modName.equals(MINECRAFT_MOD) || this.denylistMods.contains(modName)) {
                return false;
            }

            var fullItemId = ItemUtils.getNamespaceAndPath(itemStack);

            if (this.allowlistItems.contains(fullItemId)) {
                return true;
            }

            var itemPath = ItemUtils.getPath(itemStack);

            for (var allowedName : this.allowlistNames) {
                if (itemPath.contains(allowedName.toLowerCase())) {
                    return true;
                }
            }

            return false;
        }

        public void refreshCalculatedConfigFields() {
            var result = new HashMap<String, String>();

            for (var modAlias : this.modAliases) {
                if (modAlias.matches(".+?=.+")) {
                    var tokens = modAlias.toLowerCase().split("=");

                    result.put(tokens[0], tokens[1]);
                }
            }

            this.modAliasesMap = result;
        }

        private ArrayList<String> getDefaultModDenyList() {
            var result = new ArrayList<String>();

            return result;
        }

        private ArrayList<String> getDefaultAllowListItems() {
            var result = new ArrayList<String>();

            result.add("modern_industrialization:wrench");
            result.add("techreborn:wrench");
            result.add("supplementaries:wrench");
            result.add("ae2:network_tool");
            result.add("create:wrench");

            return result;
        }

        private ArrayList<String> getDefaultAllowListNames() {
            var result = new ArrayList<String>();

            result.add("wrench");
            result.add("screwdriver");
            result.add("hammer");
            result.add("rotator");
            result.add("configurator");
            result.add("crowbar");

            return result;
        }

        private ArrayList<String> getDefaultModAliases() {
            var result = new ArrayList<String>();

            result.add("createaddition=create");

            return result;
        }
    }
}
