package com.tristankechlo.toolleveling.config.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.config.util.CodecHelper;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EnchantmentOptions(
        List<Enchantment> whitelist,
        List<Enchantment> blacklist,
        short globalEnchantmentCap,
        Map<Enchantment, Short> enchantmentCaps,
        double globalUpgradeCostMultiplier,
        Map<Enchantment, Double> enchantmentUpgradeCostModifier,
        short globalMinimumEnchantmentLevel,
        Map<Enchantment, Short> minimumEnchantmentLevels
) {

    public static final Codec<List<Enchantment>> ENCHANTMENT_LIST = Registry.ENCHANTMENT.byNameCodec().listOf();
    public static final Codec<Map<Enchantment, Short>> ENCHANTMENT_TO_SHORT = Codec.unboundedMap(
            Registry.ENCHANTMENT.byNameCodec(),
            CodecHelper.POSITIVE_SHORT
    );
    public static final Codec<Map<Enchantment, Double>> ENCHANTMENT_TO_DOUBLE = Codec.unboundedMap(
            Registry.ENCHANTMENT.byNameCodec(),
            CodecHelper.PERCENTAGE
    );

    public static final Codec<EnchantmentOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ENCHANTMENT_LIST.fieldOf("enchantment_whitelist").forGetter(EnchantmentOptions::whitelist),
                    ENCHANTMENT_LIST.fieldOf("enchantment_blacklist").forGetter(EnchantmentOptions::blacklist),
                    CodecHelper.NON_NEGATIVE_SHORT.fieldOf("global_enchantment_cap").forGetter(EnchantmentOptions::globalEnchantmentCap),
                    ENCHANTMENT_TO_SHORT.fieldOf("enchantment_caps").forGetter(EnchantmentOptions::enchantmentCaps),
                    CodecHelper.PERCENTAGE.fieldOf("global_upgrade_cost_multiplier").forGetter(EnchantmentOptions::globalUpgradeCostMultiplier),
                    ENCHANTMENT_TO_DOUBLE.fieldOf("enchantment_upgrade_cost_modifier").forGetter(EnchantmentOptions::enchantmentUpgradeCostModifier),
                    CodecHelper.NON_NEGATIVE_SHORT.fieldOf("global_minimum_enchantment_level").forGetter(EnchantmentOptions::globalMinimumEnchantmentLevel),
                    ENCHANTMENT_TO_SHORT.fieldOf("minimum_enchantment_levels").forGetter(EnchantmentOptions::minimumEnchantmentLevels)
            ).apply(instance, EnchantmentOptions::new)
    );

    public static final EnchantmentOptions DEFAULT = new EnchantmentOptions(
            List.of(),
            getDefaultEnchantmentBlacklist(),
            (short) 0,
            getDefaultEnchantmentCaps(),
            1.0D,
            getDefaultEnchantmentModifier(),
            (short) 0,
            getDefaultMinimumEnchantmentLevels()
    );

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

    private static Map<Enchantment, Short> getDefaultEnchantmentCaps() {
        Map<Enchantment, Short> enchantmentCaps = new HashMap<>();
        enchantmentCaps.put(Enchantments.FIRE_PROTECTION, (short) 100);
        return enchantmentCaps;
    }

    private static Map<Enchantment, Double> getDefaultEnchantmentModifier() {
        Map<Enchantment, Double> enchantmentModifier = new HashMap<>();
        enchantmentModifier.put(Enchantments.MOB_LOOTING, 1.5D);
        return enchantmentModifier;
    }

    private static Map<Enchantment, Short> getDefaultMinimumEnchantmentLevels() {
        return Map.of();
    }

}
