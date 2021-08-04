package com.tristankechlo.toolleveling.client.screen.widgets;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.fmlclient.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class ItemValueEntry extends ObjectSelectionList.Entry<ItemValueEntry> {

	private final ItemValueScreen screen;
	private final NonNullList<Tuple<ItemStack, Long>> list;
	private static Component NARRATION = null;

	public ItemValueEntry(ItemValueScreen screen, NonNullList<Tuple<ItemStack, Long>> list) {
		this.screen = screen;
		if (list.size() != ItemValuesListWidget.ROW_SIZE) {
			throw new IllegalArgumentException("the size of the list needs to be " + ItemValuesListWidget.ROW_SIZE);
		}
		this.list = list;
	}

	@Override
	public void render(PoseStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
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

	private void renderItemTooltip(PoseStack mStack, ItemStack iStack, int mouseX, int mouseY, long worth) {
		Font font = RenderProperties.get(iStack).getFont(iStack);
		font = (font == null) ? screen.getFontRenderer() : font;
		GuiUtils.preItemToolTip(iStack);
		List<Component> tooltips = screen.getTooltipFromItem(iStack);
		tooltips.add(new TranslatableComponent("screen.toolleveling.item_value_worth", worth)
				.withStyle(ChatFormatting.DARK_GRAY));
		screen.renderComponentToolTip(mStack, tooltips, mouseX, mouseY, font);
		GuiUtils.postItemToolTip();
	}

	private boolean isMouseOverItem(int left, int top, int mouseX, int mouseY) {
		return mouseX > left && mouseX <= (left + 16) && mouseY > top && mouseY <= (top + 16);
	}

	@Override
	public Component getNarration() {
		if (NARRATION == null) {
			NARRATION = new TranslatableComponent("screen." + Names.MOD_ID + ".item_values");
		}
		return NARRATION;
	}

}
