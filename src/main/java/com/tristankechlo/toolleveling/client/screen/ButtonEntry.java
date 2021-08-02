package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.ButtonHelper.ButtonStatus;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonEntry extends ExtendedList.AbstractListEntry<ButtonEntry> {

	public Button button;
	public Enchantment enchantment;
	public String name;
	public int currentLevel;
	public long upgradeCost;
	public ButtonStatus status;
	private ToolLevelingTableScreen screen;

	public ButtonEntry(ToolLevelingTableScreen screen, Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.currentLevel = level;
		this.name = enchantment.getDescriptionId();
		this.status = ButtonStatus.NORMAL;
		this.screen = screen;
		this.upgradeCost = Utils.getEnchantmentUpgradeCost(level + 1);

		this.button = new Button(0, 0, 121, 20, ButtonHelper.getButtonText(this), (b) -> {
			// send new data to server
			PacketHandler.INSTANCE.sendToServer(new SetEnchantmentToolLevelingTable(this.screen.getMenu().getPos(),
					this.enchantment, this.currentLevel + 1));
		});
	}

	@Override
	public void render(MatrixStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean isMouseOver, float partialTicks) {

		this.button.x = left;
		this.button.y = top;
		long worth = this.screen.getMenu().getContainerWorth() + this.screen.getMenu().getBonusPoints();
		this.button.active = (this.upgradeCost <= worth) && ButtonHelper.shouldButtonBeActive(this);
		this.button.render(mStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.button.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public void updateButtonText() {
		this.button.setMessage(ButtonHelper.getButtonText(this));
	}
}
