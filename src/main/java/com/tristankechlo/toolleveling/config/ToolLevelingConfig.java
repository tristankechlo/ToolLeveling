package com.tristankechlo.toolleveling.config;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.config.misc.ConfigHelper;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public final class ToolLevelingConfig {

	public static double upgradeCostMultiplier;
	public static int minUpgradeCost;
	public static int defaultItemWorth;
	public static boolean allowLevelingUselessEnchantments;
	public static boolean allowLevelingBreakingEnchantments;
	public static boolean allowWrongEnchantments;
	public static boolean allowIncompatibleEnchantments;
	public static int globalEnchantmentCap;
	public static List<Enchantment> enchantmentWhitelist;
	public static List<Enchantment> enchantmentBlacklist;
	public static Map<Enchantment, Integer> enchantmentCaps;
	private static List<String> rawEnchantmentWhitelist;
	private static List<String> rawEnchantmentBlacklist;
	private static Map<String, Integer> rawEnchantmentCaps;
	private static final Type typeBlacklist = new TypeToken<List<String>>() {}.getType();
	private static final Type typeCaps = new TypeToken<Map<String, Integer>>() {}.getType();
	private static Gson GSON = new Gson();

	private ToolLevelingConfig() {
	}

	public static void setToDefaultValues() {
		upgradeCostMultiplier = 1.0D;
		minUpgradeCost = 1000;
		defaultItemWorth = 10;
		allowLevelingUselessEnchantments = true;
		allowLevelingBreakingEnchantments = true;
		allowIncompatibleEnchantments = true;
		allowWrongEnchantments = true;
		globalEnchantmentCap = 0;

		rawEnchantmentWhitelist = new ArrayList<>();
		rawEnchantmentBlacklist = new ArrayList<>();
		rawEnchantmentCaps = new HashMap<>();

		rawEnchantmentBlacklist.add(Enchantments.MENDING.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.AQUA_AFFINITY.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.CHANNELING.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.BINDING_CURSE.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.VANISHING_CURSE.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.FLAME.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.INFINITY.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.MULTISHOT.getRegistryName().toString());
		rawEnchantmentBlacklist.add(Enchantments.SILK_TOUCH.getRegistryName().toString());

		rawEnchantmentCaps.put(Enchantments.FIRE_PROTECTION.getRegistryName().toString(), 100);

	}

	public static JsonObject serialize(JsonObject json) {
		String url = "https://github.com/tristankechlo/Tool-Leveling/wiki/Config";
		json.addProperty("_comment", "explanation to the config structure can be found here: " + url);
		json.addProperty(Names.CONFIG.UPGRADE_COST_MULTIPLIER, upgradeCostMultiplier);
		json.addProperty(Names.CONFIG.MIN_UPGRADE_COST, minUpgradeCost);
		json.addProperty(Names.CONFIG.ALLOW_INCOMPATIBLE_ENCHANTMENTS, allowIncompatibleEnchantments);
		json.addProperty(Names.CONFIG.ALLOW_WRONG_ENCHANTMENTS, allowWrongEnchantments);
		json.addProperty(Names.CONFIG.DEFAULT_ITEM_WORTH, defaultItemWorth);
		json.addProperty(Names.CONFIG.ALLOW_LEVELING_USELESS_ENCHANTMENTS, allowLevelingUselessEnchantments);
		json.addProperty(Names.CONFIG.ALLOW_LEVELING_BREAKING_ENCHANTMENTS, allowLevelingBreakingEnchantments);
		json.addProperty(Names.CONFIG.GLOBAL_ENCHANTMENT_CAP, globalEnchantmentCap);

		JsonElement whitelist = GSON.toJsonTree(rawEnchantmentWhitelist, typeBlacklist);
		json.add(Names.CONFIG.ENCHANTMENT_WHITELIST, whitelist);

		JsonElement blacklist = GSON.toJsonTree(rawEnchantmentBlacklist, typeBlacklist);
		json.add(Names.CONFIG.ENCHANTMENT_BLACKLIST, blacklist);

		JsonElement caps = GSON.toJsonTree(rawEnchantmentCaps, typeCaps);
		json.add(Names.CONFIG.ENCHANTMENT_CAPS, caps);

		return json;
	}

	public static void deserialize(JsonObject json) {
		upgradeCostMultiplier = ConfigHelper.getInRange(json, Names.CONFIG.UPGRADE_COST_MULTIPLIER, 0.0D, 100.0D, 1.0D);
		minUpgradeCost = ConfigHelper.getInRange(json, Names.CONFIG.MIN_UPGRADE_COST, 1, Short.MAX_VALUE, 10);
		defaultItemWorth = ConfigHelper.getInRange(json, Names.CONFIG.DEFAULT_ITEM_WORTH, 1, Short.MAX_VALUE, 10);
		allowIncompatibleEnchantments = ConfigHelper.getOrDefault(json, Names.CONFIG.ALLOW_INCOMPATIBLE_ENCHANTMENTS, true);
		allowWrongEnchantments = ConfigHelper.getOrDefault(json, Names.CONFIG.ALLOW_WRONG_ENCHANTMENTS, false);
		allowLevelingUselessEnchantments = ConfigHelper.getOrDefault(json, Names.CONFIG.ALLOW_LEVELING_USELESS_ENCHANTMENTS, false);
		allowLevelingBreakingEnchantments = ConfigHelper.getOrDefault(json, Names.CONFIG.ALLOW_LEVELING_BREAKING_ENCHANTMENTS, false);
		globalEnchantmentCap = ConfigHelper.getInRange(json, Names.CONFIG.GLOBAL_ENCHANTMENT_CAP, 0, Short.MAX_VALUE, 0);

		rawEnchantmentWhitelist = GSON.fromJson(json.get(Names.CONFIG.ENCHANTMENT_WHITELIST), typeBlacklist);
		if(rawEnchantmentWhitelist == null) {
			rawEnchantmentWhitelist = new ArrayList<>();
		}
		createEnchantmentWhitelist();

		rawEnchantmentBlacklist = GSON.fromJson(json.get(Names.CONFIG.ENCHANTMENT_BLACKLIST), typeBlacklist);
		if(rawEnchantmentBlacklist == null) {
			rawEnchantmentBlacklist = new ArrayList<>();
		}
		createEnchantmentBlacklist();

		rawEnchantmentCaps = GSON.fromJson(json.get(Names.CONFIG.ENCHANTMENT_CAPS), typeCaps);
		if (rawEnchantmentCaps == null) {
			rawEnchantmentCaps = new HashMap<>();
		}
		createEnchantmentCaps();
	}

	private static void createEnchantmentWhitelist() {
		enchantmentWhitelist = new ArrayList<>();
		for (String element : rawEnchantmentWhitelist) {
			ResourceLocation loc = new ResourceLocation(element);
			if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
				enchantmentWhitelist.add(ForgeRegistries.ENCHANTMENTS.getValue(loc));
			}
		}
	}

	private static void createEnchantmentBlacklist() {
		enchantmentBlacklist = new ArrayList<>();
		for (String element : rawEnchantmentBlacklist) {
			ResourceLocation loc = new ResourceLocation(element);
			if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
				enchantmentBlacklist.add(ForgeRegistries.ENCHANTMENTS.getValue(loc));
			}
		}
	}

	private static void createEnchantmentCaps() {
		enchantmentCaps = new HashMap<>();
		for (Map.Entry<String, Integer> element : rawEnchantmentCaps.entrySet()) {
			ResourceLocation loc = new ResourceLocation(element.getKey());
			int level = element.getValue();
			if (level < 1) {
				continue;
			}
			if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
				Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(loc);
				enchantmentCaps.put(ench, level);
			}
		}
	}

}
