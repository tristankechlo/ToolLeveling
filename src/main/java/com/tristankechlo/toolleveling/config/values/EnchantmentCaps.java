package com.tristankechlo.toolleveling.config.values;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
        Map<String, Short> tempCaps = enchantmentCaps.entrySet().stream().collect(
                Collectors.toMap((e) -> Registry.ENCHANTMENT.getId(e.getKey()).toString(), (e) -> e.getValue()));
        JsonElement caps = GSON.toJsonTree(tempCaps, TYPE);
        jsonObject.add(getIdentifier(), caps);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(getIdentifier());
        if (jsonElement == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalues instead");
            return;
        }
        rawEnchantmentCaps = GSON.fromJson(jsonElement, TYPE);
        if (rawEnchantmentCaps == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalues instead");
            return;
        }
        enchantmentCaps = new HashMap<>();
        for (Map.Entry<String, Short> element : rawEnchantmentCaps.entrySet()) {
            Identifier loc = Identifier.tryParse(element.getKey());
            if (loc == null) {
                ToolLeveling.LOGGER.warn("Ignoring unknown enchantment " + element.getKey() + " from " + getIdentifier());
                continue;
            }
            short level = element.getValue();
            if (level < 1) {
                continue;
            }
            if (Registry.ENCHANTMENT.containsId(loc)) {
                Enchantment ench = Registry.ENCHANTMENT.get(loc);
                enchantmentCaps.put(ench, level);
            } else {
                ToolLeveling.LOGGER.warn("Ignoring unknown enchantment " + loc + " for " + getIdentifier());
            }
        }
    }

}
