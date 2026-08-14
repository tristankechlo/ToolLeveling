package com.tristankechlo.toolleveling.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class ButtonListWidget extends ObjectSelectionList<ButtonEntry> {

    private static final int SCROLLBAR_BUFFER_RIGHT = 1;
    private static final int SCROLLBAR_WIDTH = 6;
    final ToolLevelingTableScreen screen;
    private final int rowLeft;
    private final int rowRight;
    private final int entryWidth;

    public ButtonListWidget(ToolLevelingTableScreen screen, int x, int y, int width, int height) {
        super(screen.getMinecraft(), 0, 0, 0, 0, 22);
        this.screen = screen;
        this.width = width;
        this.height = height;
        this.x0 = x;
        this.x1 = x + width;
        this.y0 = y;
        this.y1 = y + height;

        this.rowLeft = this.x0 + 1;
        this.rowRight = this.x1 - SCROLLBAR_WIDTH - 1 - SCROLLBAR_BUFFER_RIGHT;
        this.entryWidth = rowRight - rowLeft;

        // disable rendering of the dirt background
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
        this.setRenderHeader(false, 0);
    }

    public void refreshList() {
        this.clearEntries();
        ItemStack stack = this.screen.getMenu().getSlot(0).getItem();
        if (!stack.getItem().equals(Items.AIR)) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                ButtonEntry buttonEntry = ButtonHelper.getButtonEntry(this, entry.getKey(), entry.getValue());
                this.addEntry(buttonEntry);
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float ticks) {
        this.renderBackground(poseStack);
        this.renderScrollBar();
        this.renderList(poseStack, this.width, this.height, mouseX, mouseY, ticks);
    }

    @Override
    protected void renderBackground(PoseStack poseStack) {
        GuiComponent.fill(poseStack, x0, y0, x1, y1, 0xff8B8B8B);
    }

    protected void renderList(PoseStack poseStack, int width, int height, int mouseX, int mouseY, float partialTicks) {
        int itemCount = this.getItemCount();

        for (int i = 0; i < itemCount; i++) {

            int rowTop = this.getRowTop(i);
            int rowBottom = rowTop + this.itemHeight;

            if (rowBottom >= this.y0 && rowTop <= this.y1) { // checks if any part of the item is visible
                ButtonEntry entry = this.getEntry(i);
                boolean isMouseInBounds = this.isMouseOver(mouseX, mouseY);
                entry.render(poseStack, i, rowTop, this.getRowLeft(), this.getRowWidth(), 22, mouseX, mouseY, isMouseInBounds, partialTicks);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        this.updateScrollingState(mouseX, mouseY, key);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        ButtonEntry entry = this.getEntryAtClick(mouseX, mouseY);
        if (entry != null) {
            this.setFocused(entry);
            this.setSelected(entry);
            return entry.mouseClicked(mouseX, mouseY, key);
        }
        return false;
    }

    protected final ButtonEntry getEntryAtClick(double mouseX, double mouseY) {
        int left = this.getRowLeft() + 1;
        int right = this.getRowRight() - 1;

        double adjustedMouseY = mouseY - this.y0 + 1 + this.getScrollAmount() - 2F;
        int index = (int) (adjustedMouseY / this.itemHeight);

        double relativeY = adjustedMouseY % this.itemHeight;
        boolean isWithinPadding = relativeY < 1 || relativeY > this.itemHeight - 1;

        return mouseX >= left && mouseX <= right && index >= 0 && adjustedMouseY >= 0
                && index < this.getItemCount() && !isWithinPadding
                ? this.children().get(index)
                : null;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - SCROLLBAR_WIDTH - SCROLLBAR_BUFFER_RIGHT;
    }

    @Override
    public int getRowWidth() {
        return this.entryWidth;
    }

    @Override
    protected int getRowTop(int i) {
        return this.y0 + 1 - (int) this.getScrollAmount() + i * this.itemHeight;
    }

    @Override
    public int getRowLeft() {
        return this.rowLeft;
    }

    @Override
    public int getRowRight() {
        return this.rowRight;
    }

    @Override
    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight;
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0) + 2);
    }

    private void renderScrollBar() {
        final int scrollbarX0 = this.getScrollbarPosition();
        final int scrollbarX1 = scrollbarX0 + SCROLLBAR_WIDTH;
        final int scrollbarY0 = this.y0 + 1;
        final int scrollbarY1 = this.y1 - 1;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            // RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int scrollerHeight = (int) ((float) ((scrollbarY1 - scrollbarY0) * (scrollbarY1 - scrollbarY0)) / (float) this.getMaxPosition());
            scrollerHeight = Mth.clamp(scrollerHeight, 32, scrollbarY1 - scrollbarY0 - 8);

            int scrollbarThumbPosition = (int) this.getScrollAmount() * (scrollbarY1 - scrollbarY0 - scrollerHeight) / maxScroll + scrollbarY0;

            if (scrollbarThumbPosition < scrollbarY0) {
                scrollbarThumbPosition = scrollbarY0;
            }

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(scrollbarX0, scrollbarY1, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarX1, scrollbarY1, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarX1, scrollbarY0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarX0, scrollbarY0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarX0, scrollbarThumbPosition + scrollerHeight, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarX1, scrollbarThumbPosition + scrollerHeight, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarX1, scrollbarThumbPosition, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarX0, scrollbarThumbPosition, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarX0, scrollbarThumbPosition + scrollerHeight - 1, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(scrollbarX1 - 1, scrollbarThumbPosition + scrollerHeight - 1, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(scrollbarX1 - 1, scrollbarThumbPosition, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(scrollbarX0, scrollbarThumbPosition, 0.0).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
    }

}
