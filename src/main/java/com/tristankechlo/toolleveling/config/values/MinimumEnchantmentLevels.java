package com.tristankechlo.toolleveling.config.values;

import com.google.gson.reflect.TypeToken;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class MinimumEnchantmentLevels extends RegistryMapConfig<Enchantment, Short> {

    private static final Type TYPE = new TypeToken<Map<String, Short>>() {}.getType();

    public MinimumEnchantmentLevels(String identifier) {
        super(identifier, Registry.ENCHANTMENT, getDefaults());
    }

    @Override
    protected Type getType() {
        return TYPE;
    }

    @Override
    protected boolean isValueValid(Short value) {
        return value >= 0;
    }

    @Override
    protected boolean isKeyValid(Enchantment key, Identifier identifier) {
        return key != null;
    }

    private static Map<Enchantment, Short> getDefaults() {
        return new HashMap<>();
    }

}
