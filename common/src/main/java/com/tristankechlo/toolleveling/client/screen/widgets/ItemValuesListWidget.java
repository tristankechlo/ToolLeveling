package com.tristankechlo.toolleveling.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Either;
import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import com.tristankechlo.toolleveling.utils.CustomItemStack;
import com.tristankechlo.toolleveling.utils.Utils;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemValuesListWidget extends ObjectSelectionList<ItemValueEntry> {

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int ENTRIES_PER_ROW = 9;
    private final ItemValueScreen screen;

    public ItemValuesListWidget(ItemValueScreen screen, int x, int y, int height) {
        super(screen.getMinecraft(), 0, 0, 0, 0, 18);
        this.screen = screen;
        this.width = width();
        this.height = height;
        this.x0 = x;
        this.x1 = x + width;
        this.y0 = y;
        this.y1 = y + height;

        // disable rendering of the dirt background
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
        this.setRenderHeader(false, 0);
        this.headerHeight = 0;

        this.refreshList();
    }

    private void refreshList() {
        List<NonNullList<CustomItemStack>> values = ItemValueConfig.get().values().entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(ItemValuesListWidget::mapEntryToItemStack)
                .filter((list) -> list != null && !list.isEmpty())
                .toList();

        // split into n smaller lists
        List<List<NonNullList<CustomItemStack>>> partitions = new ArrayList<>();
        for (int i = 0; i < values.size(); i += ENTRIES_PER_ROW) {
            partitions.add(values.subList(i, Math.min(i + ENTRIES_PER_ROW, values.size())));
        }

        // add as entries
        for (List<NonNullList<CustomItemStack>> list : partitions) {
            this.addEntry(new ItemValueEntry(screen, list));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        this.renderScrollBar();
        this.renderList(poseStack, this.width, this.height, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBackground(PoseStack poseStack) {
        //GuiComponent.fill(poseStack, x0, y0, x1, y1, 0xff22ff22);
    }

    private void renderScrollBar() {
        int scrollbarPositionX0 = this.getScrollbarPosition();
        int scrollbarPositionX1 = scrollbarPositionX0 + SCROLLBAR_WIDTH;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int $$15 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            $$15 = Mth.clamp($$15, 32, this.y1 - this.y0 - 8);
            int $$16 = (int) this.getScrollAmount() * (this.y1 - this.y0 - $$15) / maxScroll + this.y0;
            if ($$16 < this.y0) {
                $$16 = this.y0;
            }

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(scrollbarPositionX0, this.y1, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX1, this.y1, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX1, this.y0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX0, this.y0, 0.0).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX0, $$16 + $$15, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX1, $$16 + $$15, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX1, $$16, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX0, $$16, 0.0).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX0, $$16 + $$15 - 1, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX1 - 1, $$16 + $$15 - 1, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX1 - 1, $$16, 0.0).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(scrollbarPositionX0, $$16, 0.0).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
    }

    protected void renderList(PoseStack poseStack, int width, int height, int mouseX, int mouseY, float partialTicks) {
        int itemCount = this.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            int rowTop = this.getRowTop(i);
            int rowBottom = this.getRowTop(i) + this.itemHeight - 2;
            if (rowBottom >= this.y0 && rowTop <= this.y1) {
                ItemValueEntry entry = this.getEntry(i);
                boolean isMouseInBounds = mouseX >= this.x0 && mouseX <= this.x1 && mouseY >= this.y0 && mouseY <= this.y1;
                entry.render(poseStack, i, rowTop, this.getRowLeft(), this.getRowWidth(), this.itemHeight, mouseX, mouseY, isMouseInBounds, partialTicks);
            }
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - SCROLLBAR_WIDTH;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getRowTop(int i) {
        return this.y0 - (int) this.getScrollAmount() + i * this.itemHeight;
    }

    @Override
    public int getRowLeft() {
        return this.x0;
    }

    @Override
    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    @Override
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0));
    }

    private static NonNullList<CustomItemStack> mapEntryToItemStack(Map.Entry<Either<Item, TagKey<Item>>, Long> entry) {
        Either<Item, TagKey<Item>> key = entry.getKey();
        if (key.left().isPresent()) {
            NonNullList<CustomItemStack> list = NonNullList.withSize(1, CustomItemStack.EMPTY);
            long worth = Utils.getItemWorth(key.left().get());
            list.set(0, new CustomItemStack(key.left().get(), worth));
            return list;
        } else if (key.right().isEmpty()) {
            return null;
        }
        List<Item> items = ItemValueConfig.getAllFromTag(key.right().get());
        if (items.isEmpty()) {
            return null;
        }
        NonNullList<CustomItemStack> list = NonNullList.withSize(items.size(), CustomItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            long worth = Utils.getItemWorth(items.get(i));
            list.set(i, new CustomItemStack(items.get(i), worth, key.right().get().location()));
        }
        return list;
    }

    public static int width() {
        return (ENTRIES_PER_ROW * 18) + SCROLLBAR_WIDTH;
    }

}
