package com.tristankechlo.toolleveling.client.screen;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class ItemValueEntry extends ExtendedList.AbstractListEntry<ItemValueEntry> {

	private final ItemValueScreen screen;
	private final NonNullList<Tuple<ItemStack, Long>> list;

	public ItemValueEntry(ItemValueScreen screen, NonNullList<Tuple<ItemStack, Long>> list) {
		this.screen = screen;
		if (list.size() != ItemValuesListWidget.ROW_SIZE) {
			throw new IllegalArgumentException("the size of the list needs to be " + ItemValuesListWidget.ROW_SIZE);
		}
		this.list = list;
	}

	@Override
	public void render(MatrixStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean isMouseOver, float partialTicks) {

		int indexWidth = entryWidth / ItemValuesListWidget.ROW_SIZE;
		for (int i = 0; i < list.size(); i++) {
			int x = left + (i * indexWidth);
			int y = top + 3;
			Tuple<ItemStack, Long> tuple = list.get(i);
			screen.getMinecraft().getItemRenderer().renderGuiItem(tuple.getA(), x, y);
			if (isMouseOverItem(x, y, mouseX, mouseY)) {
				this.renderItemTooltip(mStack, tuple.getA(), mouseX, mouseY, tuple.getB());
			}
		}
	}

	private void renderItemTooltip(MatrixStack mStack, ItemStack iStack, int mouseX, int mouseY, long worth) {
		FontRenderer font = iStack.getItem().getFontRenderer(iStack);
		font = (font == null) ? screen.getFontRenderer() : font;
		GuiUtils.preItemToolTip(iStack);
		List<ITextComponent> tooltips = screen.getTooltipFromItem(iStack);
		tooltips.add(new TranslationTextComponent("screen.toolleveling.item_value_worth", worth)
				.withStyle(TextFormatting.DARK_GRAY));
		screen.renderWrappedToolTip(mStack, tooltips, mouseX, mouseY, font);
		GuiUtils.postItemToolTip();
	}

	private boolean isMouseOverItem(int left, int top, int mouseX, int mouseY) {
		return mouseX > left && mouseX <= (left + 16) && mouseY > top && mouseY <= (top + 16);
	}

}
