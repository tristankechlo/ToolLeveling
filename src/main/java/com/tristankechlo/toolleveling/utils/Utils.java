package com.tristankechlo.toolleveling.utils;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.tristankechlo.toolleveling.config.ItemValues;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Utils {

	public static final Map<Enchantment, Integer> BREAKING_ENCHANTMENTS = ImmutableMap.<Enchantment, Integer>builder()
			.put(Enchantments.LUCK_OF_THE_SEA, 84).put(Enchantments.QUICK_CHARGE, 5).put(Enchantments.THORNS, 7)
			.put(Enchantments.LURE, 5).build();

	public static Enchantment getEnchantmentFromString(String name) {
		Identifier loc = new Identifier(String.valueOf(name));
		if (Registry.ENCHANTMENT.containsId(loc)) {
			return Registry.ENCHANTMENT.get(loc);
		}
		return null;
	}

	public static long getEnchantmentUpgradeCost(Enchantment enchantment, int level) {
		double globalModifier = ToolLevelingConfig.globalUpgradeCostMultiplier.getValue();
		double specificModifier = ToolLevelingConfig.enchantmentUpgradeCostModifier.getMap().getOrDefault(enchantment,
				1.0D);
		long minCost = ToolLevelingConfig.minUpgradeCost.getValue();
		// formula: (0.87x^2 + 300x) * specificModifier * globalModifier
		return (long) Math.max(minCost, ((0.87 * level * level) + (300 * level)) * specificModifier * globalModifier);
	}

	public static Item getItemFromString(String name) {
		Identifier loc = new Identifier(String.valueOf(name));
		if (Registry.ITEM.containsId(loc)) {
			return Registry.ITEM.get(loc);
		}
		return null;
	}

	public static long getItemWorth(Item item) {
		if (ItemValues.itemValues.containsKey(item)) {
			return ItemValues.itemValues.get(item);
		}
		return ToolLevelingConfig.defaultItemWorth.getValue();
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
			if (ToolLevelingConfig.enchantmentCaps.getMap().containsKey(enchantment)) {
				short enchantmentCap = ToolLevelingConfig.enchantmentCaps.getMap().get(enchantment);
				if (enchantmentCap < globalEnchantmentCap) {
					return level >= enchantmentCap;
				}
			}
			return level >= globalEnchantmentCap;
		}
		if (ToolLevelingConfig.enchantmentCaps.getMap().containsKey(enchantment)) {
			return (level >= ToolLevelingConfig.enchantmentCaps.getMap().get(enchantment));
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
