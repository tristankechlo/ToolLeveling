package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class Util {

    public static float getSuccessChance(AbstractContainerMenu menu) {
        return getSuccessChance(menu.getSlot(1).getItem(), menu.getSlot(2).getItem(), menu.getSlot(3).getItem());
    }

    public static float getSuccessChance(ItemStack stack1, ItemStack stack2, ItemStack stack3) {
        float minChance = ToolLevelingConfig.minSuccessChance.get() / 100F;
        float maxChance = ToolLevelingConfig.maxSuccessChance.get() / 100F;
        int count = stack1.getCount() + stack2.getCount() + stack3.getCount();
        if (count == 0) {
            return minChance;
        }
        int maxCount = stack1.getMaxStackSize() + stack2.getMaxStackSize() + stack3.getMaxStackSize();
        float fullPercent = count / (float) maxCount;
        return minChance + ((maxChance - minChance) * fullPercent);
    }

    public static int getCycles(Container menu) {
        int count = 1;
        for (int i = 7; i <= 9; i++) {
            if (Predicates.ITEM_EXTRA_ENCHANTMENT.test(menu.getItem(i))) {
                count++;
            }
        }
        return count;
    }

    public static int getLevels(Container menu) {
        int count = 1;
        for (int i = 7; i <= 9; i++) {
            if (Predicates.ITEM_HIGHER_LEVEL.test(menu.getItem(i))) {
                count++;
            }
        }
        return count;
    }

}
