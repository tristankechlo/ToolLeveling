package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.tristankechlo.toolleveling.mixins.GuiComponentInvoker;
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
        GuiComponentInvoker.fillGradient(matrix4f, bufferBuilder, x, y, x + 1, y + length, m, color, color);
    }

    public static void renderHorizontalLine(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x, int y, int length, int m, int color) {
        GuiComponentInvoker.fillGradient(matrix4f, bufferBuilder, x, y, x + length, y + 1, m, color, color);
    }

    public static void renderRectangle(Matrix4f matrix4f, BufferBuilder bufferBuilder, int x, int y, int width, int height, int color) {
        GuiComponentInvoker.fillGradient(matrix4f, bufferBuilder, x, y, x + width, y + height, 0, color, color);
    }

}
