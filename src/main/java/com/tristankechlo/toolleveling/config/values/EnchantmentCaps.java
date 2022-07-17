package com.tristankechlo.toolleveling.config.values;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class EnchantmentCaps implements IConfigValue<Map<Enchantment, Short>> {

    private Map<Enchantment, Short> enchantmentCaps;
    private Map<String, Short> rawEnchantmentCaps;
    private static final Type TYPE = new TypeToken<Map<String, Short>>() {}.getType();
    public static final String IDENTIFIER = "enchantmentCaps";
    private static final Gson GSON = new Gson();

    public EnchantmentCaps() {
        this.setToDefault();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void setToDefault() {
        enchantmentCaps = new HashMap<>();
        enchantmentCaps.put(Enchantments.FIRE_PROTECTION, (short) 100);
    }

    public Map<Enchantment, Short> getMap() {
        return this.getValue();
    }

    @Override
    public Map<Enchantment, Short> getValue() {
        return enchantmentCaps;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        Map<String, Short> tempCaps = enchantmentCaps.entrySet().stream()
                .collect(Collectors.toMap((e) -> ForgeRegistries.ENCHANTMENTS.getKey(e.getKey()).toString(), (e) -> e.getValue()));
        JsonElement caps = GSON.toJsonTree(tempCaps, TYPE);
        jsonObject.add(getIdentifier(), caps);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(getIdentifier());
        if (jsonElement == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
            return;
        }
        rawEnchantmentCaps = GSON.fromJson(jsonElement, TYPE);
        if (rawEnchantmentCaps == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
            return;
        }
        enchantmentCaps = new HashMap<>();
        for (Map.Entry<String, Short> element : rawEnchantmentCaps.entrySet()) {
            ResourceLocation loc = ResourceLocation.tryParse(element.getKey());
            if (loc == null) {
                ToolLeveling.LOGGER.warn("Cannot parse enchantment " + element.getKey() + " for " + getIdentifier());
                continue;
            }
            short level = element.getValue();
            if (level < 1) {
                continue;
            }
            if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
                Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(loc);
                enchantmentCaps.put(ench, level);
            } else {
                ToolLeveling.LOGGER.warn("Ignoring unknown enchantment " + loc + " for " + getIdentifier());
            }
        }
    }

}
