package com.tristankechlo.toolleveling.client.screen.widgets;

import java.util.Map;

import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableHandledScreen;
import com.tristankechlo.toolleveling.utils.ButtonHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends ElementListWidget<ButtonEntry> {

	private final ToolLevelingTableHandledScreen screen;
	private final int listWidth;

	public ButtonListWidget(ToolLevelingTableHandledScreen screen, int listWidth, int top, int bottom) {
		super(screen.getClient(), listWidth, screen.height, top, bottom, 24);
		this.screen = screen;
		this.listWidth = listWidth;
		// disable rendering of the dirt background
		this.setRenderBackground(false);
		this.setRenderHorizontalShadows(false);
		this.setRenderHeader(false, 0);
	}

	public void refreshList() {
		this.clearEntries();
		ItemStack stack = this.screen.getScreenHandler().getSlot(0).getStack();
		long worth = this.screen.getScreenHandler().getContainerWorth()
				+ this.screen.getScreenHandler().getBonusPoints();
		if (!stack.getItem().equals(Items.AIR)) {
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
			for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
				ButtonEntry buttonEntry = ButtonHelper.getButtonEntry(this.screen, entry.getKey(), entry.getValue());
				buttonEntry.button.active = (buttonEntry.upgradeCost <= worth)
						&& ButtonHelper.shouldButtonBeActive(buttonEntry);
				this.addEntry(buttonEntry);
			}
		}
	}

	@Override
	protected int getScrollbarPositionX() {
		return this.right - 10;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	@Override
	protected void renderBackground(MatrixStack matrixStack) {
		// background of the scroll view
		this.fillGradient(matrixStack, this.left - 1, this.top - 1, this.right, this.bottom + 2, -10066330, -10066330);
	}

}
