package com.tristankechlo.toolleveling.config.values;

import com.google.gson.reflect.TypeToken;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class EnchantmentModifier extends RegistryMapConfig<Enchantment, Double> {

    private static final Type TYPE = new TypeToken<Map<String, Double>>() {}.getType();
    private static final ITagCollection<Enchantment> TAGS = TagCollectionManager.getInstance().getCustomTypeCollection(ForgeRegistries.ENCHANTMENTS);

    public EnchantmentModifier(String identifier) {
        super(identifier, ForgeRegistries.ENCHANTMENTS, TAGS, getDefaultEnchantmentModifier());
    }

    @Override
    protected Type getType() {
        return TYPE;
    }

    @Override
    protected boolean isKeyValid(Enchantment key, ResourceLocation identifier) {
        return true;
    }

    @Override
    protected boolean isValueValid(Double value) {
        return value >= 0.0;
    }

    private static Map<Enchantment, Double> getDefaultEnchantmentModifier() {
        Map<Enchantment, Double> enchantmentModifier = new HashMap<>();
        enchantmentModifier.put(Enchantments.MOB_LOOTING, 1.5D);
        return enchantmentModifier;
    }

}

