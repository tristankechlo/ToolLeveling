package com.tristankechlo.toolleveling.client.screen;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonEntry;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonListWidget;
import com.tristankechlo.toolleveling.screenhandler.ToolLevelingTableScreenhandler;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.Names;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ToolLevelingTableHandledScreen extends HandledScreen<ToolLevelingTableScreenhandler> {

	private static final Identifier TEXTURE = new Identifier(Names.MOD_ID, "textures/gui/tool_leveling_table.png");
	protected ButtonListWidget buttonList;
	private byte ticksSinceUpdate = 0;

	public ToolLevelingTableHandledScreen(ToolLevelingTableScreenhandler handler, PlayerInventory inventory,
			Text title) {
		super(handler, inventory, title);
		backgroundWidth = 248;
		backgroundHeight = 220;
		titleX -= 1;
		playerInventoryTitleY += 52;
	}

	@Override
	protected void handledScreenTick() {
		this.ticksSinceUpdate++;
		if (this.ticksSinceUpdate % 5 == 0) {
			this.ticksSinceUpdate = 0;
			this.buttonList.refreshList();
		}
	}

	@Override
	protected void init() {
		super.init();
		this.buttonList = new ButtonListWidget(this, 136, this.y + 23, this.y + 118);
		this.buttonList.setLeftPos(this.x + 105);
		this.addSelectableChild(buttonList);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		drawTexture(matrices, this.x, this.y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth,
				backgroundHeight);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		this.buttonList.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		drawMouseoverTooltip(matrices, mouseX, mouseY);
		// render button tooltips
		for (int i = 0; i < this.buttonList.children().size(); i++) {
			ButtonEntry entry = this.buttonList.children().get(i);
			if (entry.button.isFocused() || entry.button.isHovered()) {
				List<Text> tooltip = ButtonHelper.getButtonToolTips(entry);
				this.renderTooltip(matrices, tooltip, mouseX, mouseY);
			}
		}
		this.renderPointsSummary(matrices);
	}

	private void renderPointsSummary(MatrixStack stack) {
		String start = "container.toolleveling.tool_leveling_table.worth.";
		Text bonusPoints = Text.translatable(start + "bonus_points", this.getScreenHandler().getBonusPoints());
		Text invWorth = Text.translatable(start + "inv", this.getScreenHandler().getContainerWorth());
		float left = this.x + 8;
		this.textRenderer.draw(stack, bonusPoints, left, y + 45, 4210752);
		this.textRenderer.draw(stack, invWorth, left, y + 56, 4210752);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		this.buttonList.mouseScrolled(mouseX, mouseY, amount);
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		this.buttonList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	public MinecraftClient getClient() {
		return this.client;
	}

}
