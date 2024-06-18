package renektoff.refabricated_necessities.rendering.highlight;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import renektoff.refabricated_necessities.items.FeralFlareTorchItem;

public class BlockHighlighter {
    public static BlockEntity target = null;

    private static void renderHighlight(BlockEntity blockEntity, WorldRenderContext context) {
        var tessellator = Tessellator.getInstance();
        var buffer = tessellator.getBuffer();
        var vertexBuffer = new VertexBuffer();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        final float size = 1f;
        final double x = blockEntity.getPos().getX(), y = blockEntity.getPos().getY(), z = blockEntity.getPos().getZ();

        final float red = 0f;
        final float green = 0f;
        final float blue = 1f;

        var opacity = 1f;

        buffer.vertex(x, y + size, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z).color(red, green, blue, opacity).next();

        // BOTTOM
        buffer.vertex(x + size, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y, z).color(red, green, blue, opacity).next();

        // Edge 1
        buffer.vertex(x + size, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z + size).color(red, green, blue, opacity).next();

        // Edge 2
        buffer.vertex(x + size, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x + size, y + size, z).color(red, green, blue, opacity).next();

        // Edge 3
        buffer.vertex(x, y, z + size).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z + size).color(red, green, blue, opacity).next();

        // Edge 4
        buffer.vertex(x, y, z).color(red, green, blue, opacity).next();
        buffer.vertex(x, y + size, z).color(red, green, blue, opacity).next();

        vertexBuffer.bind();
        vertexBuffer.upload(buffer.end());
        VertexBuffer.unbind();

        Vec3d view = MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        var matrix = context.matrixStack();
        matrix.push();
        matrix.translate(-view.x, -view.y, -view.z);

        vertexBuffer.bind();
        vertexBuffer.draw(matrix.peek().getPositionMatrix(), new Matrix4f(context.projectionMatrix()), RenderSystem.getShader());
        VertexBuffer.unbind();
        matrix.pop();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void onRender(WorldRenderContext worldRenderContext) {
        if (target == null) {
            return;
        }

        renderHighlight(target, worldRenderContext);
    }

    public static void updateHighlightedBlock(MinecraftClient minecraftClient) {
        if (minecraftClient.world != null && minecraftClient.player != null) {
            var itemStack = minecraftClient.player.getStackInHand(minecraftClient.player.getActiveHand());

            if (itemStack != null && itemStack.getItem() instanceof FeralFlareTorchItem feralFlareTorchItem) {
                if (minecraftClient.world != null && feralFlareTorchItem.doesBoundLanternExist(itemStack, minecraftClient.world)) {
                    BlockHighlighter.target = FeralFlareTorchItem.getValidBoundLanternEntity(itemStack, minecraftClient.world);
                } else {
                    BlockHighlighter.target = null;
                }
            } else {
                BlockHighlighter.target = null;
            }
        }
    }
}