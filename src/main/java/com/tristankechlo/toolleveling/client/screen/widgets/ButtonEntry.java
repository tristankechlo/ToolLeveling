package com.tristankechlo.toolleveling.client.screen.widgets;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableHandledScreen;
import com.tristankechlo.toolleveling.network.ClientNetworkHandler;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.ButtonHelper.ButtonStatus;
import com.tristankechlo.toolleveling.utils.Utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;

@Environment(EnvType.CLIENT)
public class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {

	public ButtonWidget button;
	public Enchantment enchantment;
	public String name;
	public int currentLevel;
	public long upgradeCost;
	public ButtonStatus status;
	private ToolLevelingTableHandledScreen screen;
	private final List<ClickableWidget> list;

	public ButtonEntry(ToolLevelingTableHandledScreen screen, Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.currentLevel = level;
		this.name = enchantment.getTranslationKey();
		this.status = ButtonStatus.NORMAL;
		this.screen = screen;
		this.upgradeCost = Utils.getEnchantmentUpgradeCost(level + 1);

		this.button = new ButtonWidget(0, 0, 121, 20, ButtonHelper.getButtonText(this), b -> {
			ClientNetworkHandler.sendSetEnchantmentLevel(this.screen.getScreenHandler().getPos(), this.enchantment,
					this.currentLevel + 1);
		});
		this.list = ImmutableList.of(this.button);
	}

	@Override
	public void render(MatrixStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean isMouseOver, float partialTicks) {

		this.button.x = left;
		this.button.y = top;
		long worth = this.screen.getScreenHandler().getContainerWorth()
				+ this.screen.getScreenHandler().getBonusPoints();
		boolean normallyActive = (this.upgradeCost <= worth) && ButtonHelper.shouldButtonBeActive(this);
				this.button.active = normallyActive || Utils.freeCreativeUpgrades(MinecraftClient.getInstance().player);
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

	@Override
	public List<? extends Element> children() {
		return this.list;
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return this.list;
	}

}
