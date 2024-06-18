package renektoff.refabricated_necessities.events.utility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import renektoff.refabricated_necessities.ModItems;
import renektoff.refabricated_necessities.RefabricatedNecessities;
import renektoff.refabricated_necessities.events.morphingtool.MorphingToolEventsHandler;
import renektoff.refabricated_necessities.items.MorphTool;
import renektoff.refabricated_necessities.networking.ClientPacketSender;
import renektoff.refabricated_necessities.rendering.components.MultilineTooltipComponent;
import renektoff.refabricated_necessities.rendering.highlight.BlockHighlighter;
import renektoff.refabricated_necessities.tooltips.MultilineTooltipData;
import renektoff.refabricated_necessities.utils.TextUtils;

import java.util.ArrayList;

public class ClientInventoryEventsHandler {
    private static ItemStack lastHeldMainHandItemStack = null;
    private static ItemStack lastHeldOffHandItemStack = null;

    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }

        ClientTickEvents.START_CLIENT_TICK.register(ClientInventoryEventsHandler::onClientTick);

        ClientInventoryEvents.HELD_ITEM_CHANGE.register(ClientInventoryEventsHandler::onHeldItemChange);
        ClientInventoryEvents.ITEM_DROP.register(ClientInventoryEventsHandler::onItemDrop);
        ClientInventoryEvents.HOTBAR_SCROLL.register(ClientInventoryEventsHandler::onHotbarScroll);

        TooltipComponentCallback.EVENT.register(ClientInventoryEventsHandler::onItemTooltip);

        initialized = true;
    }

    private static void onClientTick(MinecraftClient minecraftClient) {
        if (minecraftClient.world != null && minecraftClient.player != null) {
            var mainHandItemStack = minecraftClient.player.getStackInHand(Hand.MAIN_HAND);

            if (mainHandItemStack != lastHeldMainHandItemStack) {
                ClientInventoryEvents.HELD_ITEM_CHANGE.invoker().onHeldItemChange(minecraftClient, lastHeldMainHandItemStack, mainHandItemStack, Hand.MAIN_HAND);

                lastHeldMainHandItemStack = mainHandItemStack;
            }

            var offHandItemStack = minecraftClient.player.getStackInHand(Hand.OFF_HAND);

            if (offHandItemStack != lastHeldOffHandItemStack) {
                ClientInventoryEvents.HELD_ITEM_CHANGE.invoker().onHeldItemChange(minecraftClient, lastHeldOffHandItemStack, offHandItemStack, Hand.OFF_HAND);

                lastHeldOffHandItemStack = offHandItemStack;
            }
        }
    }

    private static void onHeldItemChange(MinecraftClient minecraftClient, ItemStack previousItem, ItemStack newItem, Hand hand) {
        BlockHighlighter.updateHighlightedBlock(minecraftClient);
    }

    private static void onItemDrop(MinecraftClient minecraftClient, ItemStack itemStack, boolean dropWholeStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (minecraftClient.player != null && minecraftClient.player.isSneaking() && itemStack != null && !itemStack.isEmpty() && MorphTool.isMorphTool(itemStack) && itemStack.getItem() != ModItems.MORPHING_TOOL) {
            ClientPacketSender.sendToolRemoveItemPacket(true);

            //Cancels regular throw event, preventing the morph tool from being dropped.
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    private static void onHotbarScroll(MinecraftClient minecraftClient, ItemStack itemStack, int direction, CallbackInfo callbackInfo) {
        if (minecraftClient.player.isSneaking() && MorphTool.isMorphTool(itemStack) && (itemStack.getItem() != ModItems.MORPHING_TOOL || MorphTool.hasAnyTools(itemStack))) {
            callbackInfo.cancel();

            var newModName = MorphTool.getAdjacentModName(itemStack, direction);

            MorphingToolEventsHandler.autoTransform = newModName.equals(RefabricatedNecessities.MOD_ID);

            ClientPacketSender.sendToolScrollPacket(direction);
        }
    }

    private static TooltipComponent onItemTooltip(TooltipData tooltipData) {
        if (tooltipData instanceof MultilineTooltipData multilineTooltipData) {
            var result = new ArrayList<MutableText>();

            if (multilineTooltipData.requireShift && !Screen.hasShiftDown()) {
                result.add(Text.translatable("general.refabricated_necessities.press_shift"));
            } else {
                if (multilineTooltipData.lines.isEmpty()) {
                    if (multilineTooltipData.noLinesTranslationKey != null && !multilineTooltipData.noLinesTranslationKey.isEmpty()) {
                        result.addAll(TextUtils.wrapText(Text.translatable(multilineTooltipData.noLinesTranslationKey).getString()));
                    }
                } else {
                    if (multilineTooltipData.linesVisibleInformationTranslationKey != null && !multilineTooltipData.linesVisibleInformationTranslationKey.isEmpty()) {
                        result.addAll(TextUtils.wrapText(Text.translatable(multilineTooltipData.linesVisibleInformationTranslationKey).getString()));
                    }

                    result.addAll(multilineTooltipData.lines);
                }
            }

            return new MultilineTooltipComponent(result);
        }

        return TooltipComponent.of(OrderedText.empty());
    }
}
