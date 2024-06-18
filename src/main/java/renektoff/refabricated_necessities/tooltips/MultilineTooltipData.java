package renektoff.refabricated_necessities.tooltips;

import net.minecraft.client.item.TooltipData;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.List;

public class MultilineTooltipData implements TooltipData {
    public MultilineTooltipData(List<MutableText> lines, boolean requireShift, String noLinesTranslationKey, String linesVisibleInformationTranslationKey) {
        this.lines = lines;
        this.requireShift = requireShift;
        this.noLinesTranslationKey = noLinesTranslationKey;
        this.linesVisibleInformationTranslationKey = linesVisibleInformationTranslationKey;
    }

    public List<MutableText> lines = new ArrayList<>();

    public boolean requireShift = true;

    public String noLinesTranslationKey;

    public String linesVisibleInformationTranslationKey;
}
