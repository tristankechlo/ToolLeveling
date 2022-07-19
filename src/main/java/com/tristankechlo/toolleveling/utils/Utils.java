package com.tristankechlo.toolleveling.utils;

import com.google.common.collect.ImmutableMap;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;

public final class Utils {

    public static final Map<Enchantment, Integer> BREAKING_ENCHANTMENTS = ImmutableMap.<Enchantment, Integer>builder()
            .put(Enchantments.LUCK_OF_THE_SEA, 84).put(Enchantments.QUICK_CHARGE, 5).put(Enchantments.THORNS, 7)
            .put(Enchantments.LURE, 5).build();

    public static long getEnchantmentUpgradeCost(Enchantment enchantment, int level) {
        double globalModifier = ToolLevelingConfig.globalUpgradeCostMultiplier.getValue();
        double specificModifier = ToolLevelingConfig.enchantmentUpgradeCostModifier.getValue().getOrDefault(enchantment, 1.0D);
        long minCost = ToolLevelingConfig.minimumUpgradeCost.getValue();
        // formula: (0.87x^2 + 300x) * specificModifier * globalModifier
        return (long) Math.max(minCost, ((0.87 * level * level) + (300 * level)) * specificModifier * globalModifier);
    }

    public static long getItemWorth(Item item) {
        if (ItemValueConfig.itemValues.getValue().containsKey(item)) {
            return ItemValueConfig.itemValues.getValue().get(item);
        }
        return ItemValueConfig.defaultItemWorth.getValue();
    }

    public static long getItemWorth(ItemStack stack) {
        return Utils.getItemWorth(stack.getItem());
    }

    public static long getStackWorth(ItemStack stack) {
        return stack.getCount() * Utils.getItemWorth(stack);
    }

    public static boolean isEnchantmentAtCap(Enchantment enchantment, int level) {
        short globalEnchantmentCap = ToolLevelingConfig.globalEnchantmentCap.getValue();
        if (globalEnchantmentCap > 0) {
            if (ToolLevelingConfig.enchantmentCaps.getValue().containsKey(enchantment)) {
                short enchantmentCap = ToolLevelingConfig.enchantmentCaps.getValue().get(enchantment);
                if (enchantmentCap < globalEnchantmentCap) {
                    return level >= enchantmentCap;
                }
            }
            return level >= globalEnchantmentCap;
        }
        if (ToolLevelingConfig.enchantmentCaps.getValue().containsKey(enchantment)) {
            return (level >= ToolLevelingConfig.enchantmentCaps.getValue().get(enchantment));
        }
        return false;
    }

    public static boolean willEnchantmentBreak(Enchantment enchantment, int level) {
        if (BREAKING_ENCHANTMENTS.containsKey(enchantment)) {
            return (level >= BREAKING_ENCHANTMENTS.get(enchantment));
        }
        return false;
    }

    public static boolean freeCreativeUpgrades(PlayerEntity player) {
        return ToolLevelingConfig.freeUpgradesForCreativePlayers.getValue() && player.isCreative();
    }

}
