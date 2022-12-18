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

public final class EnchantmentCaps extends RegistryMapConfig<Enchantment, Short> {

    private static final Type TYPE = new TypeToken<Map<String, Short>>() {}.getType();
    private static final ITagCollection<Enchantment> TAGS = TagCollectionManager.getInstance().getCustomTypeCollection(ForgeRegistries.ENCHANTMENTS);

    public EnchantmentCaps(String identifier) {
        super(identifier, ForgeRegistries.ENCHANTMENTS, TAGS, getDefaultEnchantmentCaps());
    }

    private static Map<Enchantment, Short> getDefaultEnchantmentCaps() {
        Map<Enchantment, Short> enchantmentCaps = new HashMap<>();
        enchantmentCaps.put(Enchantments.FIRE_PROTECTION, (short) 100);
        return enchantmentCaps;
    }

    @Override
    protected boolean isKeyValid(Enchantment key, ResourceLocation identifier) {
        return key != null;
    }

    @Override
    protected boolean isValueValid(Short value) {
        return value >= 1;
    }

    @Override
    protected Type getType() {
        return TYPE;
    }

}

