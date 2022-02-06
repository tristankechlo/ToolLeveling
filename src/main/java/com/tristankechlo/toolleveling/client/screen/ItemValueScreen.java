package com.tristankechlo.toolleveling.client.screen;

import com.tristankechlo.toolleveling.client.screen.widgets.ItemValuesListWidget;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ItemValueScreen extends Screen {

	private static final int SPACING = 30;
	private static final Text TITLE = new TranslatableText("block.toolleveling.item_values");
	private Text defaultValue;
	private ItemValuesListWidget itemValues;

	public ItemValueScreen() {
		super(TITLE);
	}

	@Override
	protected void init() {
		super.init();
		defaultValue = new TranslatableText("screen.toolleveling.default_item_value_worth",
				ToolLevelingConfig.defaultItemWorth.getValue());
		this.itemValues = new ItemValuesListWidget(this, width - (2 * SPACING), SPACING, height - SPACING);
		this.itemValues.setLeftPos(SPACING);
		this.addSelectableChild(itemValues);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack); // render translucent grey background
		this.itemValues.render(matrixStack, mouseX, mouseY, partialTicks); // render item list widget
		super.render(matrixStack, mouseX, mouseY, partialTicks); // render buttons
		drawCenteredText(matrixStack, textRenderer, defaultValue, width / 2, 10, 0xFFFFFF);
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

	public TextRenderer getFontRenderer() {
		return this.textRenderer;
	}

	public MinecraftClient getClient() {
		return client;
	}

}
