package com.tristankechlo.toolleveling.client.screen.widgets;

import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.utils.ButtonHelper;

import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonListWidget extends ExtendedList<ButtonEntry> {

	private final ToolLevelingTableScreen screen;
	private final int listWidth;

	public ButtonListWidget(ToolLevelingTableScreen screen, int listWidth, int top, int bottom) {
		super(screen.getMinecraft(), listWidth, screen.height, top, bottom, 24);
		this.screen = screen;
		this.listWidth = listWidth;
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
				this.addEntry(ButtonHelper.getButtonEntry(this.screen, entry.getKey(), entry.getValue()));
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
	protected void renderBackground(MatrixStack matrixStack) {
		// background of the scroll view
		this.fillGradient(matrixStack, x0 - 1, y0 - 1, x1, y1 + 2, -10066330, -10066330);
	}
}