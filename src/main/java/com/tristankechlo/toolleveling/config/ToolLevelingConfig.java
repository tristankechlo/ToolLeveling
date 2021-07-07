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
import com.tristankechlo.toolleveling.config.values.BooleanValue;
import com.tristankechlo.toolleveling.config.values.ForgeRegistryConfig;
import com.tristankechlo.toolleveling.config.values.number.DoubleValue;
import com.tristankechlo.toolleveling.config.values.number.LongValue;
import com.tristankechlo.toolleveling.config.values.number.ShortValue;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public final class ToolLevelingConfig {

	public static final DoubleValue upgradeCostMultiplier;
	public static final LongValue minUpgradeCost;
	public static final LongValue defaultItemWorth;
	public static final BooleanValue allowLevelingUselessEnchantments;
	public static final BooleanValue allowLevelingBreakingEnchantments;
	public static final BooleanValue allowWrongEnchantments;
	public static final BooleanValue allowIncompatibleEnchantments;
	public static final ShortValue globalEnchantmentCap;
	public static final ForgeRegistryConfig<Enchantment> enchantmentWhitelist;
	public static final ForgeRegistryConfig<Enchantment> enchantmentBlacklist;
	public static Map<Enchantment, Short> enchantmentCaps;
	private static Map<String, Short> rawEnchantmentCaps;
	private static final Type typeCaps = new TypeToken<Map<String, Short>>() {}.getType();
	public static final String ENCHANTMENT_CAPS_NAME = "enchantmentCaps";
	private static Gson GSON = new Gson();

	static {
		upgradeCostMultiplier = new DoubleValue("upgradeCostMultiplier", 1.0D, 0.0D, 100.0D);
		minUpgradeCost = new LongValue("minUpgradeCost", 1000, 1, Long.MAX_VALUE);
		defaultItemWorth = new LongValue("defaultItemWorth", 10, 0, Long.MAX_VALUE);
		allowLevelingUselessEnchantments = new BooleanValue("allowLevelingOfUselessEnchantments", true);
		allowLevelingBreakingEnchantments = new BooleanValue("allowLevelingOfBreakingEnchanments", true);
		allowWrongEnchantments = new BooleanValue("allowWrongEnchantments", true);
		allowIncompatibleEnchantments = new BooleanValue("allowIncompatibleEnchantments", true);
		globalEnchantmentCap = new ShortValue("globalEnchantmentCap", (short) 0, (short) 0, Short.MAX_VALUE);
		enchantmentWhitelist = new ForgeRegistryConfig<>("enchantmentWhitelist", ForgeRegistries.ENCHANTMENTS,
				new ArrayList<>());
		enchantmentBlacklist = new ForgeRegistryConfig<>("enchantmentBlacklist", ForgeRegistries.ENCHANTMENTS,
				getDefaultEnchantmentBlacklist());
	}

	private ToolLevelingConfig() {}

	public static void setToDefaultValues() {
		upgradeCostMultiplier.setToDefault();
		minUpgradeCost.setToDefault();
		defaultItemWorth.setToDefault();
		allowLevelingUselessEnchantments.setToDefault();
		allowLevelingBreakingEnchantments.setToDefault();
		allowIncompatibleEnchantments.setToDefault();
		allowWrongEnchantments.setToDefault();
		globalEnchantmentCap.setToDefault();

		enchantmentWhitelist.setToDefault();
		enchantmentBlacklist.setToDefault();

		rawEnchantmentCaps = new HashMap<>();
		rawEnchantmentCaps.put(Enchantments.FIRE_PROTECTION.getRegistryName().toString(), (short) 100);
		createEnchantmentCaps();
	}

	public static JsonObject serialize(JsonObject json) {
		String url = "https://github.com/tristankechlo/Tool-Leveling/wiki/";
		json.addProperty("_comment", "explanation to the config structure can be found here: " + url);
		upgradeCostMultiplier.serialize(json);
		minUpgradeCost.serialize(json);
		allowIncompatibleEnchantments.serialize(json);
		allowWrongEnchantments.serialize(json);
		defaultItemWorth.serialize(json);
		allowLevelingUselessEnchantments.serialize(json);
		allowLevelingBreakingEnchantments.serialize(json);
		globalEnchantmentCap.serialize(json);

		enchantmentWhitelist.serialize(json);
		enchantmentBlacklist.serialize(json);

		JsonElement caps = GSON.toJsonTree(rawEnchantmentCaps, typeCaps);
		json.add(ENCHANTMENT_CAPS_NAME, caps);

		return json;
	}

	public static void deserialize(JsonObject json) {
		upgradeCostMultiplier.deserialize(json);
		minUpgradeCost.deserialize(json);
		defaultItemWorth.deserialize(json);
		allowIncompatibleEnchantments.deserialize(json);
		allowWrongEnchantments.deserialize(json);
		allowLevelingUselessEnchantments.deserialize(json);
		allowLevelingBreakingEnchantments.deserialize(json);
		globalEnchantmentCap.deserialize(json);

		enchantmentWhitelist.deserialize(json);
		enchantmentBlacklist.deserialize(json);

		rawEnchantmentCaps = GSON.fromJson(json.get(ENCHANTMENT_CAPS_NAME), typeCaps);
		if (rawEnchantmentCaps == null) {
			rawEnchantmentCaps = new HashMap<>();
		}
		createEnchantmentCaps();
	}

	private static void createEnchantmentCaps() {
		enchantmentCaps = new HashMap<>();
		for (Map.Entry<String, Short> element : rawEnchantmentCaps.entrySet()) {
			ResourceLocation loc = new ResourceLocation(element.getKey());
			short level = element.getValue();
			if (level < 1) {
				continue;
			}
			if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
				Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(loc);
				enchantmentCaps.put(ench, level);
			}
		}
	}

	private static List<Enchantment> getDefaultEnchantmentBlacklist() {
		List<Enchantment> enchantments = new ArrayList<>();
		enchantments.add(Enchantments.MENDING);
		enchantments.add(Enchantments.AQUA_AFFINITY);
		enchantments.add(Enchantments.CHANNELING);
		enchantments.add(Enchantments.BINDING_CURSE);
		enchantments.add(Enchantments.VANISHING_CURSE);
		enchantments.add(Enchantments.FLAME);
		enchantments.add(Enchantments.INFINITY);
		enchantments.add(Enchantments.MULTISHOT);
		enchantments.add(Enchantments.SILK_TOUCH);
		return enchantments;
	}

}
