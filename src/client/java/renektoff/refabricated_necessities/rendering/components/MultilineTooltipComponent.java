package renektoff.refabricated_necessities.rendering.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.function.Function;

public class MultilineTooltipComponent implements TooltipComponent {
    private final List<MutableText> lines;
    private final Function<MutableText, MutableText> defaultTextTransform;

    public MultilineTooltipComponent(List<MutableText> lines) {
        this(lines, text -> text.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
    }

    public MultilineTooltipComponent(List<MutableText> lines, Function<MutableText, MutableText> textTransform) {
        this.lines = lines;
        this.defaultTextTransform = textTransform;
    }

    @Override
    public int getHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight * this.lines.size();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        var maxWidth = 0;

        for (var line : lines) {
            var width = textRenderer.getWidth(line.getString());
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        return maxWidth;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        var currentY = y;

        for (var line : this.lines) {
            var text = line.getStyle().isEmpty() ? this.defaultTextTransform.apply(line) : line;

            textRenderer.draw(text, (float) x, (float) currentY, -1, true, matrix, vertexConsumers, false, 0, 15728880);

            currentY += textRenderer.fontHeight;
        }
    }
}
