package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.client.screen.widgets.ItemValuesListWidget;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;

public class ItemValueScreen extends Screen {

    private static final Component TITLE = Component.translatable("block.toolleveling.item_values");
    private ItemValuesListWidget itemValues;
    private Component defaultItemValueText;
    private final int titleOffsetX;
    private final int titleOffsetY;


    public ItemValueScreen() {
        super(TITLE);
        this.titleOffsetX = 10;
        this.titleOffsetY = 10;
    }

    @Override
    protected void init() {
        super.init();

        int widgetWidth = ItemValuesListWidget.width();
        int leftPos = (this.width - widgetWidth) / 2;

        this.itemValues = new ItemValuesListWidget(this, leftPos, 12, this.height - 16);
        this.addWidget(itemValues);
        this.defaultItemValueText = Component.translatable("screen.toolleveling.default_item_value_worth", ItemValueConfig.get().defaultItemWorth());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack); // render translucent grey background
        this.itemValues.render(poseStack, mouseX, mouseY, partialTicks); // render item list widget
        this.font.draw(poseStack, defaultItemValueText, titleOffsetX, titleOffsetY, ChatFormatting.WHITE.getColor());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonID) {
        this.itemValues.mouseClicked(mouseX, mouseY, buttonID);
        return super.mouseClicked(mouseX, mouseY, buttonID);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.itemValues.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

}
