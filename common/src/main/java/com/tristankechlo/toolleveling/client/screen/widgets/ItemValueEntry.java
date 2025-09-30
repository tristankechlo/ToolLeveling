package com.tristankechlo.toolleveling.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.utils.CustomItemStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemValueEntry extends ObjectSelectionList.Entry<ItemValueEntry> {

    private final ItemValueScreen screen;
    private final List<NonNullList<CustomItemStack>> list;
    private static Component NARRATION = null;
    private int counter = 0;

    public ItemValueEntry(ItemValueScreen screen, List<NonNullList<CustomItemStack>> list) {
        this.screen = screen;
        this.list = list;
    }

    @Override
    public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseInBounds, float partialTicks) {
        for (int i = 0; i < list.size(); i++) {
            int x = left + (i * 18);
            CustomItemStack customStack = getNextItemStack(list.get(i));
            screen.getItemRenderer().renderGuiItem(customStack.stack, x + 1, top + 1);
            if (isMouseOverItem(x, top, mouseX, mouseY) && isMouseInBounds) {
                renderSlotHighlight(poseStack, x, top, 0x33ffffff);
                this.renderItemTooltip(poseStack, customStack, mouseX, mouseY);
            }
        }
        counter++;
    }

    private void renderItemTooltip(PoseStack poseStack, CustomItemStack stack, int mouseX, int mouseY) {
        TooltipFlag.Default flag = screen.getMinecraft().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        List<Component> tooltips = stack.getTooltipLines(flag);
        screen.renderComponentTooltip(poseStack, tooltips, mouseX, mouseY);
    }

    private boolean isMouseOverItem(int left, int top, int mouseX, int mouseY) {
        return mouseX > left && mouseX <= (left + 18) && mouseY > top && mouseY <= (top + 18);
    }

    @Override
    public Component getNarration() {
        if (NARRATION == null) {
            NARRATION = new TranslatableComponent("screen." + ToolLeveling.MOD_ID + ".item_values");
        }
        return NARRATION;
    }

    private CustomItemStack getNextItemStack(NonNullList<CustomItemStack> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        // select next item after x renders
        int rendersPerItem = 40; // adjust to change speed
        return list.get((counter / rendersPerItem) % list.size());
    }

    private static void renderSlotHighlight(PoseStack poseStack, int x, int y, int color) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        GuiComponent.fill(poseStack, x, y, x + 18, y + 18, color);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

}
