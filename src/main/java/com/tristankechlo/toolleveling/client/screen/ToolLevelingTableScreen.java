package com.tristankechlo.toolleveling.client.screen;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToolLevelingTableScreen extends ContainerScreen<ToolLevelingTableContainer> {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Names.MOD_ID, "textures/gui/tool_leveling_table.png");
	protected ButtonListWidget buttonList;
	private byte ticksSinceUpdate = 0;

	public ToolLevelingTableScreen(ToolLevelingTableContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		// texture size
		this.xSize = 248;
		this.ySize = 220;
		// offset for player inv title
//		this.playerInventoryTitleX -= 1;
		this.playerInventoryTitleY += 52;
		// offset container title
		this.titleX -= 1;
	}

	@Override
	protected void init() {
		super.init();
		this.buttonList = new ButtonListWidget(this, 136, this.guiTop + 23, this.guiTop + 118);
		this.buttonList.setLeftPos(this.guiLeft + 105);
		this.children.add(buttonList);

	}

	@Override
	public void tick() {
		super.tick();
		this.ticksSinceUpdate++;
		if (this.ticksSinceUpdate % 2 == 0) {
			this.ticksSinceUpdate = 0;
			this.buttonList.refreshList();
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack); // render translucent grey background
		this.buttonList.render(matrixStack, mouseX, mouseY, partialTicks); // render button scroll view
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY); // render item toolltips

		// render button tooltips
		for (int i = 0; i < this.buttonList.getEventListeners().size(); i++) {
			ButtonEntry entry = this.buttonList.getEventListeners().get(i);
			if (entry.button.isHovered()) {
				List<ITextComponent> tooltip = ButtonHelper.getButtonToolTips(entry);
				this.func_243308_b(matrixStack, tooltip, mouseX, mouseY);
			}
		}

		this.renderPointsSummary(matrixStack);
	}

	private void renderPointsSummary(MatrixStack stack) {
		String start = "container.toolleveling.tool_leveling_table.worth.";
		ITextComponent bonusPoints = new TranslationTextComponent(start + "bonus_points", this.container.getBonusPoints());
		ITextComponent invWorth = new TranslationTextComponent(start + "inv", this.container.getContainerWorth());
		float left = this.guiLeft + 8;
		this.font.func_243248_b(stack, bonusPoints, left, guiTop + 45, 4210752);
		this.font.func_243248_b(stack, invWorth, left, guiTop + 56, 4210752);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		this.buttonList.mouseDragged(mouseX, mouseY, button, dragX, dragY);
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		this.buttonList.mouseScrolled(mouseX, mouseY, delta);
		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mX, int mY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
		blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
	}

}
