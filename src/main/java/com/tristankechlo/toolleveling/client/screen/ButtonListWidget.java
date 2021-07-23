package com.tristankechlo.toolleveling.client.screen;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.utils.ButtonHelper;

import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ButtonListWidget extends ObjectSelectionList<ButtonEntry> {

	private ToolLevelingTableScreen screen;
	private final int listWidth;

	public ButtonListWidget(ToolLevelingTableScreen screen, int listWidth, int top, int bottom) {
		super(screen.getMinecraft(), listWidth, screen.height, top, bottom, 24);
		this.screen = screen;
		this.listWidth = listWidth;
		// disable rendering of the dirt background
//		this.func_244605_b(false);
//		this.func_244606_c(false);
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(false);
		this.setRenderHeader(false, 0);
	}

	public void refreshList() {
		this.clearEntries();
		ItemStack stack = this.screen.getMenu().getSlot(0).getItem();
		long worth = this.screen.getMenu().getContainerWorth() + this.screen.getMenu().getBonusPoints();
		if (!stack.getItem().equals(Items.AIR)) {
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
				ButtonEntry buttonEntry = ButtonHelper.getButtonEntry(this.screen, entry.getKey(), entry.getValue());
				buttonEntry.button.active = (buttonEntry.upgradeCost <= worth) && ButtonHelper.shouldButtonBeActive(buttonEntry);
				this.addEntry(buttonEntry);
			}
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return this.x1 - 10;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	@Override
	protected void renderBackground(PoseStack matrixStack) {
		// background of the scroll view
		this.fillGradient(matrixStack, x0 - 1, y0 - 1, x1, y1 + 2, -10066330, -10066330);
	}
}