package renektoff.refabricated_necessities.items;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import renektoff.refabricated_necessities.ModItems;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.networking.ServerPacketSender;
import renektoff.refabricated_necessities.tooltips.MultilineTooltipData;
import renektoff.refabricated_necessities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class MorphTool extends Item {
    public static final String NBT_MORPHING_TOOL_KEY = "morphtool:is_morphing";
    public static final String NBT_MORPH_TOOL_DATA_KEY = "morphtool:data";
    public static final String NBT_MORPH_TOOL_DISPLAY_NAME_KEY = "morphtool:displayName";

    public MorphTool(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var pos = context.getBlockPos();
        var world = context.getWorld();

        var block = world.getBlockState(pos);

        var rotation = BlockRotation.CLOCKWISE_90;
        block.rotate(rotation);

        return super.useOnBlock(context);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        var lines = new ArrayList<MutableText>();

        var nbt = stack.getNbt();

        if (nbt != null) {
            var morphData = nbt.getCompound(NBT_MORPH_TOOL_DATA_KEY);

            if (morphData != null) {
                for (var modNamespace : morphData.getKeys()) {
                    var modItem = morphData.getCompound(modNamespace);

                    lines.add(Text.literal(modItem.getString("id")).setStyle(Style.EMPTY.withItalic(true).withFormatting(Formatting.GREEN)));
                }
            }
        }

        var noLinesKey = "item.refabricated_necessities.morph_tool.tooltip.no_items";
        var linesVisibleInfoKey = "item.refabricated_necessities.morph_tool.tooltip.items_in_tool";

        var result = new MultilineTooltipData(lines, true, noLinesKey, linesVisibleInfoKey);

        return Optional.of(result);
    }

    public static boolean isMorphTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() == ModItems.MORPHING_TOOL) {
            return true;
        }

        var nbt = stack.getNbt();

        return nbt != null && stack.getNbt().getBoolean(NBT_MORPHING_TOOL_KEY);
    }

    public static boolean hasAnyTools(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasNbt()) {
            return false;
        }

        var toolData = stack.getNbt().getCompound(NBT_MORPH_TOOL_DATA_KEY);

        return !toolData.isEmpty();
    }

    public static void removeItemFromTool(ServerPlayerEntity player, ItemStack stack, boolean spawnEntity) {
        if (stack != null && !stack.isEmpty() && MorphTool.isMorphTool(stack) && stack.getItem() != ModItems.MORPHING_TOOL) {
            var morphingToolItemStack = transformStack(stack, RefabricatedNecessities.CONFIG.MorphingTool.MINECRAFT_MOD);

            var newMorphNbt = morphingToolItemStack.getNbt().getCompound(NBT_MORPH_TOOL_DATA_KEY);
            newMorphNbt.remove(RefabricatedNecessities.CONFIG.MorphingTool.getModNameOrAlias(ItemUtils.getNamespace(stack)));

            if (spawnEntity) {
                var newItemStack = stack.copy();
                newItemStack.removeCustomName();

                if (newItemStack.getNbt() == null) {
                    newItemStack.setNbt(new NbtCompound());
                }

                var nbt = newItemStack.getNbt();

                var ogName = nbt.getString(NBT_MORPH_TOOL_DISPLAY_NAME_KEY);

                if (ogName != null && !ogName.isEmpty() && !newItemStack.getName().getString().equals(ogName)) {
                    newItemStack.setCustomName(Text.of(ogName));
                }

                nbt.remove(NBT_MORPH_TOOL_DATA_KEY);
                nbt.remove(NBT_MORPHING_TOOL_KEY);
                nbt.remove(NBT_MORPH_TOOL_DISPLAY_NAME_KEY);

                player.dropItem(newItemStack, false, true);
            }

            var inventory = player.getInventory();
            inventory.setStack(inventory.selectedSlot, morphingToolItemStack);

            ServerPacketSender.sendMorphToolItemRemovedPacket(player);
        }
    }

    public static ItemStack transformStack(ItemStack currentStack, String targetMod) {
        if (!currentStack.hasNbt()) {
            return currentStack;
        }

        var currentMod = getCurrentMod(currentStack);

        if (currentMod.equals(targetMod)) {
            return currentStack;
        }

        var stackNbt = currentStack.getNbt();
        var morphData = stackNbt.getCompound(NBT_MORPH_TOOL_DATA_KEY).copy();

        ItemStack resultStack;

        var currentStackAsNbt = new NbtCompound();
        currentStack.writeNbt(currentStackAsNbt);
        currentStackAsNbt = currentStackAsNbt.copy();

        if (currentStackAsNbt.contains("tag")) {
            currentStackAsNbt.getCompound("tag").remove(NBT_MORPH_TOOL_DATA_KEY);
        }

        if (!currentMod.equalsIgnoreCase(RefabricatedNecessities.CONFIG.MorphingTool.MINECRAFT_MOD) && !currentMod.equalsIgnoreCase(RefabricatedNecessities.MOD_ID)) {
            morphData.put(currentMod, currentStackAsNbt);
        }

        if (targetMod.equals(RefabricatedNecessities.CONFIG.MorphingTool.MINECRAFT_MOD)) {
            resultStack = new ItemStack(ModItems.MORPHING_TOOL);
        } else {
            var targetStackAsNbt = morphData.getCompound(targetMod);
            morphData.remove(targetMod);

            resultStack = ItemStack.fromNbt(targetStackAsNbt);

            if (resultStack.isEmpty()) {
                resultStack = new ItemStack(ModItems.MORPHING_TOOL);
            }
        }

        if (!resultStack.hasNbt()) {
            resultStack.setNbt(new NbtCompound());
        }

        var resultStackNbt = resultStack.getNbt();

        resultStackNbt.put(NBT_MORPH_TOOL_DATA_KEY, morphData);
        resultStackNbt.putBoolean(NBT_MORPHING_TOOL_KEY, true);

        if (resultStack.getItem() != ModItems.MORPHING_TOOL) {
            Text displayName;

            if (resultStackNbt.contains(NBT_MORPH_TOOL_DISPLAY_NAME_KEY)) {
                displayName = Text.of(resultStackNbt.getString(NBT_MORPH_TOOL_DISPLAY_NAME_KEY));
            } else {
                displayName = resultStack.getName();

                resultStackNbt.putString(NBT_MORPH_TOOL_DISPLAY_NAME_KEY, displayName.getString());
            }

            var morphToolNameText = ModItems.MORPHING_TOOL.getName();
            var morphedItemText = displayName.getWithStyle(Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GREEN)));

            var siblings = morphToolNameText.getSiblings();

            siblings.add(Text.of(" ("));
            siblings.addAll(morphedItemText);
            siblings.add(Text.of(")"));

            resultStack.setCustomName(morphToolNameText);
        }

        resultStack.setCount(1);

        return resultStack;
    }

    public static String getAdjacentModName(ItemStack stack, int direction) {
        if (!hasAnyTools(stack)) {
            return RefabricatedNecessities.MOD_ID;
        }

        var morphData = stack.getNbt().getCompound(NBT_MORPH_TOOL_DATA_KEY);
        var currentMod = getCurrentMod(stack);

        var mods = new ArrayList<>(morphData.getKeys());

        mods.add(RefabricatedNecessities.MOD_ID);
        if (!currentMod.equals(RefabricatedNecessities.MOD_ID)) {
            mods.add(currentMod);
        }

        Collections.sort(mods);

        var id = mods.indexOf(currentMod);
        var retId = 0;

        retId = direction > 0 ? id + 1 : id - 1;

        if (retId < 0) {
            retId = mods.size() - 1;
        } else if (retId > mods.size() - 1) {
            retId = 0;
        }

        return mods.get(retId);
    }

    public static String getCurrentMod(ItemStack stack) {
        return RefabricatedNecessities.CONFIG.MorphingTool.getModNameOrAlias(ItemUtils.getNamespace(stack));
    }
}
