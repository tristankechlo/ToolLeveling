package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.values.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public final class ToolLevelingConfig {

    public static final NumberValue<Double> globalUpgradeCostMultiplier;
    public static final NumberValue<Long> minUpgradeCost;
    public static final NumberValue<Long> defaultItemWorth;
    public static final BooleanValue allowLevelingUselessEnchantments;
    public static final BooleanValue allowLevelingBreakingEnchantments;
    public static final BooleanValue allowWrongEnchantments;
    public static final BooleanValue allowIncompatibleEnchantments;
    public static final BooleanValue freeUpgradesForCreativePlayers;
    public static final NumberValue<Short> globalEnchantmentCap;
    public static final RegistryConfig<Enchantment> enchantmentWhitelist;
    public static final RegistryConfig<Enchantment> enchantmentBlacklist;
    public static final EnchantmentCaps enchantmentCaps;
    public static final EnchantmentModifier enchantmentUpgradeCostModifier;

    static {
        globalUpgradeCostMultiplier = new NumberValue<>("globalUpgradeCostMultiplier", 1.0D, 0.0D, 100.0D);
        minUpgradeCost = new NumberValue<>("minUpgradeCost", 1000L, 1L, Long.MAX_VALUE);
        defaultItemWorth = new NumberValue<>("defaultItemWorth", 10L, 0L, Long.MAX_VALUE);
        allowLevelingUselessEnchantments = new BooleanValue("allowLevelingOfUselessEnchantments", true);
        allowLevelingBreakingEnchantments = new BooleanValue("allowLevelingOfBreakingEnchanments", true);
        allowWrongEnchantments = new BooleanValue("allowWrongEnchantments", true);
        allowIncompatibleEnchantments = new BooleanValue("allowIncompatibleEnchantments", true);
        freeUpgradesForCreativePlayers = new BooleanValue("freeUpgradesForCreativePlayers", true);
        globalEnchantmentCap = new NumberValue<>("globalEnchantmentCap", (short) 0, (short) 0, Short.MAX_VALUE);
        enchantmentWhitelist = new RegistryConfig<>("enchantmentWhitelist", Registry.ENCHANTMENT, new ArrayList<>());
        enchantmentBlacklist = new RegistryConfig<>("enchantmentBlacklist", Registry.ENCHANTMENT, getDefaultEnchantmentBlacklist());
        enchantmentCaps = new EnchantmentCaps();
        enchantmentUpgradeCostModifier = new EnchantmentModifier();
    }

    private ToolLevelingConfig() {}

    public static void setToDefaultValues() {
        globalUpgradeCostMultiplier.setToDefault();
        minUpgradeCost.setToDefault();
        defaultItemWorth.setToDefault();
        allowLevelingUselessEnchantments.setToDefault();
        allowLevelingBreakingEnchantments.setToDefault();
        allowIncompatibleEnchantments.setToDefault();
        allowWrongEnchantments.setToDefault();
        freeUpgradesForCreativePlayers.setToDefault();
        globalEnchantmentCap.setToDefault();

        enchantmentWhitelist.setToDefault();
        enchantmentBlacklist.setToDefault();

        enchantmentCaps.setToDefault();
        enchantmentUpgradeCostModifier.setToDefault();
    }

    public static JsonObject serialize(JsonObject json) {
        String url = "https://github.com/tristankechlo/Tool-Leveling/wiki/";
        json.addProperty("_comment", "explanation to the config structure can be found here: " + url);
        globalUpgradeCostMultiplier.serialize(json);
        minUpgradeCost.serialize(json);
        allowIncompatibleEnchantments.serialize(json);
        allowWrongEnchantments.serialize(json);
        defaultItemWorth.serialize(json);
        allowLevelingUselessEnchantments.serialize(json);
        allowLevelingBreakingEnchantments.serialize(json);
        freeUpgradesForCreativePlayers.serialize(json);
        globalEnchantmentCap.serialize(json);

        enchantmentWhitelist.serialize(json);
        enchantmentBlacklist.serialize(json);

        enchantmentCaps.serialize(json);
        enchantmentUpgradeCostModifier.serialize(json);

        return json;
    }

    public static void deserialize(JsonObject json) {
        globalUpgradeCostMultiplier.deserialize(json);
        minUpgradeCost.deserialize(json);
        defaultItemWorth.deserialize(json);
        allowIncompatibleEnchantments.deserialize(json);
        allowWrongEnchantments.deserialize(json);
        allowLevelingUselessEnchantments.deserialize(json);
        allowLevelingBreakingEnchantments.deserialize(json);
        freeUpgradesForCreativePlayers.deserialize(json);
        globalEnchantmentCap.deserialize(json);

        enchantmentWhitelist.deserialize(json);
        enchantmentBlacklist.deserialize(json);

        enchantmentCaps.deserialize(json);
        enchantmentUpgradeCostModifier.deserialize(json);
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
