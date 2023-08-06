package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public final class CustomTooltipRenderer {

    private CustomTooltipRenderer() {}

    public static void renderBackground(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x, int y, int width, int height, int c1, int c2, int c3) {
        //outer border
        renderHorizontalLine(matrix4f, bufferBuilder, x + 2, y, width - 4, 0, c2); // top line
        renderVerticalLine(matrix4f, bufferBuilder, x, y + 2, height - 4, 0, c2); // left line
        renderHorizontalLine(matrix4f, bufferBuilder, x + 2, y + height - 1, width - 4, 0, c2); // bottom line
        renderVerticalLine(matrix4f, bufferBuilder, x + width - 1, y + 2, height - 4, 0, c2); // right line
        renderRectangle(matrix4f, bufferBuilder, x + 1, y + 1, width - 2, height - 2, c2);
        //inner border
        renderRectangle(matrix4f, bufferBuilder, x + 2, y + 1, width - 4, height - 2, c3);
        renderVerticalLine(matrix4f, bufferBuilder, x + 1, y + 2, height - 4, 0, c3); // left line
        renderVerticalLine(matrix4f, bufferBuilder, x + width - 2, y + 2, height - 4, 0, c3); // right line
        //main inner field
        renderRectangle(matrix4f, bufferBuilder, x + 3, y + 2, width - 6, height - 4, c1);
        renderVerticalLine(matrix4f, bufferBuilder, x + 2, y + 3, height - 6, 0, c1); // left line
        renderVerticalLine(matrix4f, bufferBuilder, x + width - 3, y + 3, height - 6, 0, c1); // right line
    }

    public static void renderVerticalLine(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x, int y, int length, int m, int color) {
        fillGradient(matrix4f, bufferBuilder, x, y, x + 1, y + length, m, color, color);
    }

    public static void renderHorizontalLine(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x, int y, int length, int m, int color) {
        fillGradient(matrix4f, bufferBuilder, x, y, x + length, y + 1, m, color, color);
    }

    public static void renderRectangle(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x, int y, int width, int height, int color) {
        fillGradient(matrix4f, bufferBuilder, x, y, x + width, y + height, 0, color, color);
    }

    /** copied from {@link net.minecraft.client.gui.GuiGraphics} */
    private static void fillGradient(Matrix4f matrix, BufferBuilder buffer, int x1, int y1, int x2, int y2, int m, int color1, int color2) {
        float $$9 = (float) FastColor.ARGB32.alpha(color1) / 255.0F;
        float $$10 = (float) FastColor.ARGB32.red(color1) / 255.0F;
        float $$11 = (float) FastColor.ARGB32.green(color1) / 255.0F;
        float $$12 = (float) FastColor.ARGB32.blue(color1) / 255.0F;
        float $$13 = (float) FastColor.ARGB32.alpha(color2) / 255.0F;
        float $$14 = (float) FastColor.ARGB32.red(color2) / 255.0F;
        float $$15 = (float) FastColor.ARGB32.green(color2) / 255.0F;
        float $$16 = (float) FastColor.ARGB32.blue(color2) / 255.0F;
        buffer.vertex(matrix, (float) x1, (float) y1, (float) m).color($$10, $$11, $$12, $$9).endVertex();
        buffer.vertex(matrix, (float) x1, (float) y2, (float) m).color($$14, $$15, $$16, $$13).endVertex();
        buffer.vertex(matrix, (float) x2, (float) y2, (float) m).color($$14, $$15, $$16, $$13).endVertex();
        buffer.vertex(matrix, (float) x2, (float) y1, (float) m).color($$10, $$11, $$12, $$9).endVertex();
    }

}
