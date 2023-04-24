package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class Util {

    public static float getSuccessChance(AbstractContainerMenu menu) {
        return getSuccessChance(menu.getSlot(1).getItem(), menu.getSlot(2).getItem(), menu.getSlot(3).getItem());
    }

    public static float getSuccessChance(ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        float minChance = ToolLevelingConfig.MIN_SUCCESS_CHANCE.get() / 100F;
        float maxChance = ToolLevelingConfig.MAX_SUCCESS_CHANCE.get() / 100F;
        int count = stack1.getCount() + stack2.getCount() + stack3.getCount();
        if (count == 0) {
            return minChance;
        }
        int maxCount = stack1.getMaxStackSize() + stack2.getMaxStackSize() + stack3.getMaxStackSize();
        float fullPercent = count / (float) maxCount;
        float chance = minChance + ((maxChance - minChance) * fullPercent);
        chance = Math.round(chance * 1000F) / 10F; // round to 1 decimal place
        return chance;
    }

}
