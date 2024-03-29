package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.primitives.BooleanValue;
import com.tristankechlo.toolleveling.config.primitives.DoubleValue;
import com.tristankechlo.toolleveling.config.primitives.LongValue;
import com.tristankechlo.toolleveling.config.primitives.ShortValue;
import com.tristankechlo.toolleveling.config.values.EnchantmentCaps;
import com.tristankechlo.toolleveling.config.values.EnchantmentModifier;
import com.tristankechlo.toolleveling.config.values.MinimumEnchantmentLevels;
import com.tristankechlo.toolleveling.config.values.RegistryListConfig;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class ToolLevelingConfig {

    // General options
    private static final String GENERAL_OPTIONS = "general_options";
    public static final LongValue minimumUpgradeCost = new LongValue("minimum_upgrade_cost", 1000L, 1L, Long.MAX_VALUE);
    public static final BooleanValue allowLevelingUselessEnchantments = new BooleanValue("allow_leveling_of_useless_enchantments", true);
    public static final BooleanValue allowLevelingBreakingEnchantments = new BooleanValue("allow_leveling_of_breaking_enchantments", true);
    public static final BooleanValue freeUpgradesForCreativePlayers = new BooleanValue("free_upgrades_for_creative_players", true);

    // Enchantment options
    private static final String ENCHANTMENT_OPTIONS = "enchantment_options";
    public static final RegistryListConfig<Enchantment> whitelist = new RegistryListConfig<>("enchantment_whitelist", ForgeRegistries.ENCHANTMENTS, new ArrayList<>());
    public static final RegistryListConfig<Enchantment> blacklist = new RegistryListConfig<>("enchantment_blacklist", ForgeRegistries.ENCHANTMENTS, getDefaultEnchantmentBlacklist());
    public static final ShortValue globalEnchantmentCap = new ShortValue("global_enchantment_cap", (short) 0, (short) 0, Short.MAX_VALUE);
    public static final EnchantmentCaps enchantmentCaps = new EnchantmentCaps("enchantment_caps");
    public static final DoubleValue globalUpgradeCostMultiplier = new DoubleValue("global_upgrade_cost_multiplier", 1.0D, 0.0D, 100.0D);
    public static final EnchantmentModifier enchantmentUpgradeCostModifier = new EnchantmentModifier("enchantment_upgrade_cost_modifier");
    public static final ShortValue globalMinimumEnchantmentLevel = new ShortValue("global_minimum_enchantment_level", (short) 0, (short) 0, Short.MAX_VALUE);
    public static final MinimumEnchantmentLevels minimumEnchantmentLevels = new MinimumEnchantmentLevels("minimum_enchantment_levels");

    private ToolLevelingConfig() {}

    public static void setToDefault() {
        //reset general options
        minimumUpgradeCost.setToDefault();
        allowLevelingUselessEnchantments.setToDefault();
        allowLevelingBreakingEnchantments.setToDefault();
        freeUpgradesForCreativePlayers.setToDefault();

        //reset enchantment options
        whitelist.setToDefault();
        blacklist.setToDefault();
        globalEnchantmentCap.setToDefault();
        enchantmentCaps.setToDefault();
        globalUpgradeCostMultiplier.setToDefault();
        enchantmentUpgradeCostModifier.setToDefault();
        globalMinimumEnchantmentLevel.setToDefault();
        minimumEnchantmentLevels.setToDefault();
    }

    public static JsonObject serialize(JsonObject json) {
        //serialize general options
        JsonObject general = new JsonObject();
        minimumUpgradeCost.serialize(general);
        allowLevelingUselessEnchantments.serialize(general);
        allowLevelingBreakingEnchantments.serialize(general);
        freeUpgradesForCreativePlayers.serialize(general);
        json.add(GENERAL_OPTIONS, general);

        //serialize enchantment options
        JsonObject enchantment = new JsonObject();
        whitelist.serialize(enchantment);
        blacklist.serialize(enchantment);
        globalEnchantmentCap.serialize(enchantment);
        enchantmentCaps.serialize(enchantment);
        globalUpgradeCostMultiplier.serialize(enchantment);
        enchantmentUpgradeCostModifier.serialize(enchantment);
        globalMinimumEnchantmentLevel.serialize(enchantment);
        minimumEnchantmentLevels.serialize(enchantment);
        json.add(ENCHANTMENT_OPTIONS, enchantment);
        return json;
    }

    public static void deserialize(JsonObject json) {
        //deserialize general options
        if (json.has(GENERAL_OPTIONS)) {
            JsonObject general = json.getAsJsonObject(GENERAL_OPTIONS);
            minimumUpgradeCost.deserialize(general);
            allowLevelingUselessEnchantments.deserialize(general);
            allowLevelingBreakingEnchantments.deserialize(general);
            freeUpgradesForCreativePlayers.deserialize(general);
        }

        //deserialize enchantment options
        if (json.has(ENCHANTMENT_OPTIONS)) {
            JsonObject enchantment = json.getAsJsonObject(ENCHANTMENT_OPTIONS);
            whitelist.deserialize(enchantment);
            blacklist.deserialize(enchantment);
            globalEnchantmentCap.deserialize(enchantment);
            enchantmentCaps.deserialize(enchantment);
            globalUpgradeCostMultiplier.deserialize(enchantment);
            enchantmentUpgradeCostModifier.deserialize(enchantment);
            globalMinimumEnchantmentLevel.deserialize(enchantment);
            minimumEnchantmentLevels.deserialize(enchantment);
        }
    }

    private static List<Enchantment> getDefaultEnchantmentBlacklist() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantments.MENDING);
        enchantments.add(Enchantments.AQUA_AFFINITY);
        enchantments.add(Enchantments.CHANNELING);
        enchantments.add(Enchantments.BINDING_CURSE);
        enchantments.add(Enchantments.VANISHING_CURSE);
        enchantments.add(Enchantments.FLAMING_ARROWS);
        enchantments.add(Enchantments.INFINITY_ARROWS);
        enchantments.add(Enchantments.MULTISHOT);
        enchantments.add(Enchantments.SILK_TOUCH);
        return enchantments;
    }

}
