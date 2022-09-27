package com.tristankechlo.toolleveling.client.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.ButtonHelper.ButtonStatus;
import com.tristankechlo.toolleveling.utils.Names;
import com.tristankechlo.toolleveling.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonEntry extends ObjectSelectionList.Entry<ButtonEntry> {

    public Button button;
    public Enchantment enchantment;
    public String name;
    public int currentLevel;
    public long upgradeCost;
    private ButtonStatus status = ButtonStatus.NORMAL;
    private final ToolLevelingTableScreen screen;
    private static Component NARRATION = null;

    public ButtonEntry(ToolLevelingTableScreen screen, Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.currentLevel = level;
        this.name = enchantment.getDescriptionId();
        this.screen = screen;
        this.upgradeCost = Utils.getEnchantmentUpgradeCost(enchantment, level + 1);

        this.button = new Button(0, 0, 121, 20, ButtonHelper.getButtonText(this), (b) -> {
            // send new data to server
            PacketHandler.INSTANCE.sendToServer(new SetEnchantmentToolLevelingTable(this.screen.getMenu().getPos(), this.enchantment, this.currentLevel + 1));
        });
    }

    @Override
    public void render(PoseStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
                       int mouseY, boolean isMouseOver, float partialTicks) {

        this.button.x = left;
        this.button.y = top;
        long worth = this.screen.getMenu().getContainerWorth() + this.screen.getMenu().getBonusPoints();
        boolean normallyActive = (this.upgradeCost <= worth) && ButtonHelper.shouldButtonBeActive(this);
        this.button.active = normallyActive || Utils.freeCreativeUpgrades(Minecraft.getInstance().player);
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
    public Component getNarration() {
        if (NARRATION == null) {
            NARRATION = Component.translatable("screen." + Names.MOD_ID + ".tool_leveling_table");
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
