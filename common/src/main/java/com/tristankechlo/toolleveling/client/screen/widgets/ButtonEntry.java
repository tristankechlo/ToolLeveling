package com.tristankechlo.toolleveling.client.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.ServerBoundPacketHandler;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.ButtonHelper.ButtonStatus;
import com.tristankechlo.toolleveling.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class ButtonEntry extends ObjectSelectionList.Entry<ButtonEntry> {

    private final Enchantment enchantment;
    public String name;
    public int currentLevel;
    public long upgradeCost;
    private ButtonStatus status = ButtonStatus.NORMAL;
    private final ButtonListWidget parent;
    private static Component NARRATION = null;
    private final Button button;

    public ButtonEntry(ButtonListWidget parent, Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.currentLevel = level;
        this.name = enchantment.getDescriptionId();
        this.parent = parent;
        this.upgradeCost = Utils.getEnchantmentUpgradeCost(enchantment, level + 1);

        this.button = new Button(0, 0, 121, 20, ButtonHelper.getButtonText(this), (b) -> {
            // send new data to server
            ServerBoundPacketHandler.INSTANCE.enchantAtToolLevelingTable(this.parent.screen.getMenu().getPos(), this.enchantment, this.currentLevel + 1);
        });
        this.updateButtonText();
    }

    @Override
    public void render(PoseStack poseStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOverParent, float partialTicks) {
        long worth = this.parent.screen.getMenu().getContainerWorth() + this.parent.screen.getMenu().getBonusPoints();
        boolean normallyActive = (this.upgradeCost <= worth) && ButtonHelper.shouldButtonBeActive(this);
        boolean active = normallyActive || Utils.freeCreativeUpgrades(Minecraft.getInstance().player);

        this.button.x = left + 1;
        this.button.y = top + 1;
        this.button.active = active;
        this.button.setWidth(entryWidth - 2);
        this.button.render(poseStack, mouseX, mouseY, partialTicks);

        if (button.isHoveredOrFocused()) {
            List<Component> tooltip = ButtonHelper.getButtonToolTips(this);
            this.parent.screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.button.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return super.isMouseOver(x, y);
    }

    public void updateButtonText() {
        this.button.setMessage(ButtonHelper.getButtonText(this));
    }

    @Override
    public Component getNarration() {
        if (NARRATION == null) {
            NARRATION = new TranslatableComponent("screen." + ToolLeveling.MOD_ID + ".tool_leveling_table");
        }
        return NARRATION;
    }

    public void setStatus(ButtonStatus status) {
        this.status = status;
        this.updateButtonText();
    }

    public ButtonStatus getStatus() {
        return this.status;
    }

}
