package com.tristankechlo.toolleveling.config.values;

import com.google.gson.reflect.TypeToken;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class EnchantmentModifier extends RegistryMapConfig<Enchantment, Double> {

    private static final Type TYPE = new TypeToken<Map<String, Double>>() {}.getType();

    public EnchantmentModifier(String identifier) {
        super(identifier, Registries.ENCHANTMENT, getDefaultEnchantmentModifier());
    }

    @Override
    protected Type getType() {
        return TYPE;
    }

    @Override
    protected boolean isKeyValid(Enchantment key, Identifier identifier) {
        return true;
    }

    @Override
    protected boolean isValueValid(Double value) {
        return value >= 0.0;
    }

    private static Map<Enchantment, Double> getDefaultEnchantmentModifier() {
        Map<Enchantment, Double> enchantmentModifier = new HashMap<>();
        enchantmentModifier.put(Enchantments.LOOTING, 1.5D);
        return enchantmentModifier;
    }

}
