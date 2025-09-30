package com.tristankechlo.toolleveling.client.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class ButtonListWidget extends ObjectSelectionList<ButtonEntry> {

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
                ButtonEntry buttonEntry = ButtonHelper.getButtonEntry(this.screen, entry.getKey(), entry.getValue());
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
    protected void renderBackground(PoseStack poseStack) {
        GuiComponent.fill(poseStack, x0, y0 - 1, x1, y1 + 2, 0xff8B8B8B);
    }

}
