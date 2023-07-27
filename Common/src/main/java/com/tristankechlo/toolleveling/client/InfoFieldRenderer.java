package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class InfoFieldRenderer {

    private List<Component> lines = new ArrayList<>();
    private boolean spaceAfterTitle = false; // TODO make this an int, so i can specify the amount of space after the title
    private final int backgroundColor;
    private final int borderColor1;
    private final int borderColor2;

    public InfoFieldRenderer(int backgroundColor, int borderColor1, int borderColor2) {
        this.backgroundColor = backgroundColor;
        this.borderColor1 = borderColor1;
        this.borderColor2 = borderColor2;
    }

    public void setSpaceAfterTitle(boolean spaceAfterTitle) {
        this.spaceAfterTitle = spaceAfterTitle;
    }

    public void setLines(List<Component> lines) {
        this.lines = List.copyOf(lines);
    }

    public int calcWidth(Font font) {
        int width = lines.stream().mapToInt(font::width).max().orElse(0);
        width += 8;
        return width;
    }

    public int calcHeight() {
        int height = 6 + (lines.size() >= 1 && spaceAfterTitle ? 2 : 0); // add 2 pixels for space after title, when required
        height += (lines.size() * 10);
        return height;
    }

    public void render(PoseStack poseStack, Font font, int startX, int startY) {
        render(poseStack, font, startX, startY, calcWidth(font));
    }

    public void render(PoseStack poseStack, Font font, int startX, int startY, int width) {
        // if there are no lines, add the title as the first line, otherwise return
        if (this.lines.isEmpty()) {
            return;
        }

        // calculate width and height of the info field
        int height = calcHeight();

        poseStack.pushPose();
        poseStack.translate(startX, startY, 50);
        // render background
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = poseStack.last().pose();
        CustomTooltipRenderer.renderBackground(matrix4f, bufferBuilder, 0, 0, width, height, backgroundColor, borderColor1, borderColor2);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferUploader.drawWithShader(bufferBuilder.end());

        // render text
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        poseStack.translate(4, 4, 50);
        renderLines(matrix4f, bufferSource, font, lines, spaceAfterTitle);
        bufferSource.endBatch();
        poseStack.popPose();
    }

    private static void renderLines(Matrix4f matrix4f, MultiBufferSource bufferSource, Font font, List<Component> lines, boolean spaceAfterTitle) {
        int y = 0;
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            font.drawInBatch(line, 0, y, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            y += 10 + (i == 0 && spaceAfterTitle ? 2 : 0);
        }
    }

}
