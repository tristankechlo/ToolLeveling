package com.tristankechlo.toolleveling.utils;

import com.google.common.collect.ImmutableMap;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.config.options.EnchantmentOptions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;

public final class Utils {

    public static final Map<Enchantment, Integer> BREAKING_ENCHANTMENTS = ImmutableMap.<Enchantment, Integer>builder()
            .put(Enchantments.FISHING_LUCK, 84).put(Enchantments.QUICK_CHARGE, 5).put(Enchantments.THORNS, 7)
            .put(Enchantments.FISHING_SPEED, 5).build();

    public static long getEnchantmentUpgradeCost(Enchantment enchantment, int level) {
        EnchantmentOptions options = ToolLevelingConfig.get().enchantmentOptions();
        double globalModifier = options.globalUpgradeCostMultiplier();
        double specificModifier = options.enchantmentUpgradeCostModifier().getOrDefault(enchantment, 1.0D);
        long minCost = ToolLevelingConfig.get().generalOptions().minimumUpgradeCost();
        // formula: (0.87x^2 + 300x) * specificModifier * globalModifier
        return (long) Math.max(minCost, ((0.87 * level * level) + (300 * level)) * specificModifier * globalModifier);
    }

    public static long getItemWorth(Item item) {
        if (ItemValueConfig.get().itemValues().containsKey(item)) {
            return ItemValueConfig.get().itemValues().get(item);
        }
        return ItemValueConfig.get().defaultItemWorth();
    }

    public static long getItemWorth(ItemStack stack) {
        return Utils.getItemWorth(stack.getItem());
    }

    public static long getStackWorth(ItemStack stack) {
        return stack.getCount() * Utils.getItemWorth(stack);
    }

    /**
     * if global cap is not set, use the enchantment specific one<br>
     * if global cap is set, use the lowest of the two
     */
    public static boolean isEnchantmentAtCap(Enchantment enchantment, int level) {
        EnchantmentOptions options = ToolLevelingConfig.get().enchantmentOptions();
        short globalEnchantmentCap = options.globalEnchantmentCap();
        // global cap is set, use the lowest of the two
        if (globalEnchantmentCap > 0) {
            if (options.enchantmentCaps().containsKey(enchantment)) {
                short enchantmentCap = options.enchantmentCaps().get(enchantment);
                if (enchantmentCap < globalEnchantmentCap) {
                    return level >= enchantmentCap;
                }
            }
            return level >= globalEnchantmentCap;
        }
        // global cap is not set, use the enchantment specific one
        if (options.enchantmentCaps().containsKey(enchantment)) {
            return (level >= options.enchantmentCaps().get(enchantment));
        }
        return false;
    }

    /**
     * if global minimum is not set, use the enchantment specific one<br>
     * if global minimum is set, use the highest of the two
     */
    public static boolean isEnchantmentOverMinimum(Enchantment enchantment, int level) {
        EnchantmentOptions options = ToolLevelingConfig.get().enchantmentOptions();
        short globalEnchantmentMin = options.globalMinimumEnchantmentLevel();
        // global minimum is set, use the highest of the two
        if (globalEnchantmentMin > 0) {
            if (options.minimumEnchantmentLevels().containsKey(enchantment)) {
                short enchantmentMin = options.minimumEnchantmentLevels().get(enchantment);
                if (enchantmentMin > globalEnchantmentMin) {
                    return level >= enchantmentMin;
                }
            }
            return level >= globalEnchantmentMin;
        }
        // global minimum is not set, use the enchantment specific one
        if (options.minimumEnchantmentLevels().containsKey(enchantment)) {
            return level >= options.minimumEnchantmentLevels().get(enchantment);
        }
        return level >= globalEnchantmentMin;
    }

    public static boolean willEnchantmentBreak(Enchantment enchantment, int level) {
        if (BREAKING_ENCHANTMENTS.containsKey(enchantment)) {
            return (level >= BREAKING_ENCHANTMENTS.get(enchantment));
        }
        return false;
    }

    public static boolean freeCreativeUpgrades(Player player) {
        return ToolLevelingConfig.get().generalOptions().freeUpgradesForCreativePlayers() && player.isCreative();
    }

}
