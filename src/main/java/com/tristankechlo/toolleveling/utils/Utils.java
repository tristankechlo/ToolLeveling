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
			.put(Enchantments.LUCK_OF_THE_SEA, 84).put(Enchantments.QUICK_CHARGE, 5).put(Enchantments.THORNS, 7)
			.put(Enchantments.LURE, 5).build();

	public static Enchantment getEnchantmentFromString(String name) {
		ResourceLocation loc = new ResourceLocation(String.valueOf(name));
		if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
			return ForgeRegistries.ENCHANTMENTS.getValue(loc);
		}
		return null;
	}

	public static int getEnchantmentUpgradeCost(int level) {
		double modifier = ToolLevelingConfig.upgradeCostMultiplier;
		int minCost = ToolLevelingConfig.minUpgradeCost;
		// formula: (0.0015x^4 + 300x) * modifier
		// link to the graph
		// https://www.desmos.com/calculator/e2mglowz80
		return (int) Math.max(minCost, (Math.pow((0.0015 * level), 4) + (300 * level)) * modifier);
	}

	public static Item getItemFromString(String name) {
		ResourceLocation loc = new ResourceLocation(String.valueOf(name));
		if (ForgeRegistries.ITEMS.containsKey(loc)) {
			return ForgeRegistries.ITEMS.getValue(loc);
		}
		return null;
	}

	public static int getItemWorth(Item item) {
		if (ItemValues.itemValues.containsKey(item)) {
			return ItemValues.itemValues.get(item);
		}
		return ToolLevelingConfig.defaultItemWorth;
	}

	public static int getItemWorth(ItemStack stack) {
		return Utils.getItemWorth(stack.getItem());
	}

	public static int getStackWorth(ItemStack stack) {
		return stack.getCount() * Utils.getItemWorth(stack);
	}

	public static int getInventoryWorth(Inventory inv) {
		int worth = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			worth += Utils.getStackWorth(inv.getStackInSlot(i));
		}
		return worth;
	}

	public static boolean isEnchantmentAtCap(Enchantment enchantment, int level) {
		if (ToolLevelingConfig.globalEnchantmentCap > 0) {
			return level >= ToolLevelingConfig.globalEnchantmentCap;
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
