package com.tristankechlo.toolleveling.config;

import net.minecraft.world.item.ItemStack;

public enum BonusType {

    HIGHER_LEVEL,
    EXTRA_ENCHANTMENT,
    NONE;

    public static BonusType getTypeByItem(ItemStack stack) {
        if (ToolLevelingConfig.isBonusItemStrength(stack)) {
            return HIGHER_LEVEL;
        } else if (ToolLevelingConfig.isBonusItemIterations(stack)) {
            return EXTRA_ENCHANTMENT;
        } else {
            return NONE;
        }
    }

}
