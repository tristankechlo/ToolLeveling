package com.tristankechlo.toolleveling.client.screen.widgets;

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

import java.util.List;

@Environment(EnvType.CLIENT)
public class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {

    public ButtonWidget button;
    public Enchantment enchantment;
    public String name;
    public int currentLevel;
    public long upgradeCost;
    private ButtonStatus status = ButtonStatus.NORMAL;
    private final ToolLevelingTableHandledScreen screen;
    private final List<ClickableWidget> list;

    public ButtonEntry(ToolLevelingTableHandledScreen screen, Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.currentLevel = level;
        this.name = enchantment.getTranslationKey();
        this.screen = screen;
        this.upgradeCost = Utils.getEnchantmentUpgradeCost(enchantment, level + 1);

        this.button = new ButtonWidget.Builder(ButtonHelper.getButtonText(this), (b) -> {
            ClientNetworkHandler.sendSetEnchantmentLevel(this.screen.getScreenHandler().getPos(), this.enchantment, this.currentLevel + 1);
        }).position(0, 0).size(121, 20).build();
        this.list = ImmutableList.of(this.button);
    }

    @Override
    public void render(MatrixStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
                       int mouseY, boolean isMouseOver, float partialTicks) {

        this.button.setX(left);
        this.button.setY(top);
        long worth = this.screen.getScreenHandler().getContainerWorth() + this.screen.getScreenHandler().getBonusPoints();
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

    public void setStatus(ButtonStatus status) {
        this.status = status;
        this.updateButtonText();
    }

    public ButtonStatus getStatus() {
        return this.status;
    }

}
