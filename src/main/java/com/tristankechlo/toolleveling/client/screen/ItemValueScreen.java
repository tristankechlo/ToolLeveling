package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.client.screen.widgets.ItemValuesListWidget;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemValueScreen extends Screen {

	private static final int SPACING = 30;
	private static final Component TITLE = new TranslatableComponent("block.toolleveling.item_values");
	private ItemValuesListWidget itemValues;

	public ItemValueScreen() {
		super(TITLE);
	}

	@Override
	protected void init() {
		super.init();
		this.itemValues = new ItemValuesListWidget(this, width - (2 * SPACING), SPACING, height - SPACING);
		this.itemValues.setLeftPos(SPACING);
		this.addWidget(itemValues);
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack); // render translucent grey background
		this.itemValues.render(matrixStack, mouseX, mouseY, partialTicks); // render item list widget
		super.render(matrixStack, mouseX, mouseY, partialTicks); // render buttons
		drawCenteredString(matrixStack, font, new TranslatableComponent("screen.toolleveling.default_item_value_worth",
				ToolLevelingConfig.defaultItemWorth.getValue()), width / 2, 10, 0xFFFFFF);
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

	public Font getFontRenderer() {
		return this.font;
	}

}
