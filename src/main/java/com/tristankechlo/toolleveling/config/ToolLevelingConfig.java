package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.primitives.BooleanValue;
import com.tristankechlo.toolleveling.config.primitives.DoubleValue;
import com.tristankechlo.toolleveling.config.primitives.LongValue;
import com.tristankechlo.toolleveling.config.primitives.ShortValue;
import com.tristankechlo.toolleveling.config.values.EnchantmentCaps;
import com.tristankechlo.toolleveling.config.values.EnchantmentModifier;
import com.tristankechlo.toolleveling.config.values.RegistryListConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public final class ToolLevelingConfig {

    // General options
    private static final String GENERAL_OPTIONS = "general_options";
    public static final LongValue minimumUpgradeCost = new LongValue("minimumUpgradeCost", 1000L, 1L, Long.MAX_VALUE);
    public static final BooleanValue allowLevelingUselessEnchantments = new BooleanValue("allowLevelingOfUselessEnchantments", true);
    public static final BooleanValue allowLevelingBreakingEnchantments = new BooleanValue("allowLevelingOfBreakingEnchantments", true);
    public static final BooleanValue freeUpgradesForCreativePlayers = new BooleanValue("freeUpgradesForCreativePlayers", true);

    // Enchantment options
    private static final String ENCHANTMENT_OPTIONS = "enchantment_options";
    public static final RegistryListConfig<Enchantment> whitelist = new RegistryListConfig<>("enchantmentWhitelist", Registry.ENCHANTMENT, new ArrayList<>());
    public static final RegistryListConfig<Enchantment> blacklist = new RegistryListConfig<>("enchantmentBlacklist", Registry.ENCHANTMENT, getDefaultEnchantmentBlacklist());
    public static final ShortValue globalEnchantmentCap = new ShortValue("globalEnchantmentCap", (short) 0, (short) 0, Short.MAX_VALUE);
    public static final EnchantmentCaps enchantmentCaps = new EnchantmentCaps("enchantmentCaps");
    public static final DoubleValue globalUpgradeCostMultiplier = new DoubleValue("globalUpgradeCostMultiplier", 1.0D, 0.0D, 100.0D);
    public static final EnchantmentModifier enchantmentUpgradeCostModifier = new EnchantmentModifier("enchantmentUpgradeCostModifier");

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
        json.add(ENCHANTMENT_OPTIONS, enchantment);
        return json;
    }

    public static void deserialize(JsonObject json) {
        if (!json.has(GENERAL_OPTIONS)) {
            throw new IllegalArgumentException("Missing general options");
        }
        if (!json.has(ENCHANTMENT_OPTIONS)) {
            throw new IllegalArgumentException("Missing enchantment options");
        }
        //deserialize general options
        JsonObject general = json.getAsJsonObject(GENERAL_OPTIONS);
        minimumUpgradeCost.deserialize(general);
        allowLevelingUselessEnchantments.deserialize(general);
        allowLevelingBreakingEnchantments.deserialize(general);
        freeUpgradesForCreativePlayers.deserialize(general);

        //deserialize enchantment options
        JsonObject enchantment = json.getAsJsonObject(ENCHANTMENT_OPTIONS);
        whitelist.deserialize(enchantment);
        blacklist.deserialize(enchantment);
        globalEnchantmentCap.deserialize(enchantment);
        enchantmentCaps.deserialize(enchantment);
        globalUpgradeCostMultiplier.deserialize(enchantment);
        enchantmentUpgradeCostModifier.deserialize(enchantment);
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
