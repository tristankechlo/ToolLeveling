package com.tristankechlo.toolleveling.config;

import net.minecraft.world.item.ItemStack;

public enum BonusType {

    HIGHER_LEVEL,
    EXTRA_ENCHANTMENT,
    NONE;

    public static BonusType getTypeByItem(ItemStack stack) {
        if (ToolLevelingConfig.isBonusItemHigherLevel(stack)) {
            return HIGHER_LEVEL;
        } else if (ToolLevelingConfig.isBonusItemExtraEnchantment(stack)) {
            return EXTRA_ENCHANTMENT;
        } else {
            return NONE;
        }
    }

}
