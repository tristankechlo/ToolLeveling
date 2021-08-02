package com.tristankechlo.toolleveling.utils;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.tristankechlo.toolleveling.config.ItemValues;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {

	public static final Map<Enchantment, Integer> BREAKING_ENCHANTMENTS = ImmutableMap.<Enchantment, Integer>builder()
			.put(Enchantments.FISHING_LUCK, 84).put(Enchantments.QUICK_CHARGE, 5).put(Enchantments.THORNS, 7)
			.put(Enchantments.FISHING_SPEED, 5).build();

	public static Enchantment getEnchantmentFromString(String name) {
		ResourceLocation loc = new ResourceLocation(String.valueOf(name));
		if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
			return ForgeRegistries.ENCHANTMENTS.getValue(loc);
		}
		return null;
	}

	public static long getEnchantmentUpgradeCost(int level) {
		double modifier = ToolLevelingConfig.upgradeCostMultiplier.getValue();
		long minCost = ToolLevelingConfig.minUpgradeCost.getValue();
		// formula: (0.0015x^4 + 300x) * modifier
		// link to the graph
		// https://www.desmos.com/calculator/e2mglowz80
		return (long) Math.max(minCost, (Math.pow((0.0015 * level), 4) + (300 * level)) * modifier);
	}

	public static Item getItemFromString(String name) {
		ResourceLocation loc = new ResourceLocation(String.valueOf(name));
		if (ForgeRegistries.ITEMS.containsKey(loc)) {
			return ForgeRegistries.ITEMS.getValue(loc);
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

	public static long getInventoryWorth(Inventory inv) {
		long worth = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			worth += Utils.getStackWorth(inv.getItem(i));
		}
		return worth;
	}

	public static boolean isEnchantmentAtCap(Enchantment enchantment, int level) {
		short globalEnchantmentCap = ToolLevelingConfig.globalEnchantmentCap.getValue();
		if (globalEnchantmentCap > 0) {
			if (ToolLevelingConfig.enchantmentCaps.containsKey(enchantment)) {
				short enchantmentCap = ToolLevelingConfig.enchantmentCaps.get(enchantment);
				if (enchantmentCap < globalEnchantmentCap) {
					return level >= enchantmentCap;
				}
			}
			return level >= globalEnchantmentCap;
		}
		if (ToolLevelingConfig.enchantmentCaps.containsKey(enchantment)) {
			return (level >= ToolLevelingConfig.enchantmentCaps.get(enchantment));
		}
		return false;
	}

	public static boolean willEnchantmentBreak(Enchantment enchantment, int level) {
		if (BREAKING_ENCHANTMENTS.containsKey(enchantment)) {
			return (level >= BREAKING_ENCHANTMENTS.get(enchantment));
		}
		return false;
	}
}
