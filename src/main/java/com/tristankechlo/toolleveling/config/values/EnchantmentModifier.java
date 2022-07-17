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

public final class EnchantmentModifier implements IConfigValue<Map<Enchantment, Double>> {

    private Map<Enchantment, Double> enchantmentModifier;
    private Map<String, Double> rawEnchantmentModifier;
    private static final Type TYPE = new TypeToken<Map<String, Double>>() {}.getType();
    public static final String IDENTIFIER = "enchantmentUpgradeCostModifier";
    private static final Gson GSON = new Gson();

    public EnchantmentModifier() {
        this.setToDefault();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void setToDefault() {
        enchantmentModifier = new HashMap<>();
        enchantmentModifier.put(Enchantments.MOB_LOOTING, 1.5D);
    }

    @Override
    public Map<Enchantment, Double> getValue() {
        return enchantmentModifier;
    }

    public Map<Enchantment, Double> getMap() {
        return this.getValue();
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        Map<String, Double> tempUpgradeCosts = enchantmentModifier.entrySet().stream()
                .collect(Collectors.toMap((e) -> ForgeRegistries.ENCHANTMENTS.getKey(e.getKey()).toString(), (e) -> e.getValue()));
        JsonElement modifier = GSON.toJsonTree(tempUpgradeCosts, TYPE);
        jsonObject.add(getIdentifier(), modifier);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(getIdentifier());
        if (jsonElement == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
            return;
        }
        rawEnchantmentModifier = GSON.fromJson(jsonElement, TYPE);
        if (rawEnchantmentModifier == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
            return;
        }
        enchantmentModifier = new HashMap<>();
        for (Map.Entry<String, Double> element : rawEnchantmentModifier.entrySet()) {
            ResourceLocation loc = ResourceLocation.tryParse(element.getKey());
            if (loc == null) {
                ToolLeveling.LOGGER.warn("Cannot parse enchantment " + element.getKey() + " for " + getIdentifier());
                continue;
            }
            double modifier = element.getValue();
            if (modifier < 1) {
                continue;
            }
            if (ForgeRegistries.ENCHANTMENTS.containsKey(loc)) {
                Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(loc);
                enchantmentModifier.put(ench, modifier);
            } else {
                ToolLeveling.LOGGER.warn("Ignoring unknown enchantment " + loc + " for " + getIdentifier());
            }
        }
    }

}
