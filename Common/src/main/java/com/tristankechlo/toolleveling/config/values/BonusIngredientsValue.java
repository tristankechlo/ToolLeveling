package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public final class BonusIngredientsValue extends AbstractConfigValue<BonusIngredient[]> {

    private BonusIngredient[] value;
    private final BonusIngredient[] defaultValue;

    public BonusIngredientsValue(String name, BonusIngredient[] defaultValue) {
        super(name);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    @Override
    public void setToDefault() {
        this.value = defaultValue;
    }

    @Override
    public void serialize(JsonObject json) {
        JsonArray arr = new JsonArray();
        for (BonusIngredient bonus : value) {
            JsonObject obj = new JsonObject();
            BonusIngredient.serialize(bonus, obj);
            arr.add(obj);
        }
        json.add(getIdentifier(), arr);
    }

    @Override
    public void deserialize(JsonObject json) {
        if (json.has("bonus_item_more_enchantments") && json.has("bonus_item_more_levels")) {
            // migrate from one ingredient per bonus
            ToolLeveling.LOGGER.info("Migrating for the config value " + getIdentifier());
            try {
                JsonObject moreEnchantsObj = GsonHelper.getAsJsonObject(json, "bonus_item_more_enchantments");
                Ingredient maxLevelBonusIngredient = Ingredient.fromJson(moreEnchantsObj);
                JsonObject moreLevelsObj = GsonHelper.getAsJsonObject(json, "bonus_item_more_levels");
                Ingredient iterationsBonusIngredient = Ingredient.fromJson(moreLevelsObj);
                value = new BonusIngredient[]{
                    new BonusIngredient(maxLevelBonusIngredient, 0.0F, 0.0F, 1.0F),
                    new BonusIngredient(iterationsBonusIngredient, 0.0F, 1.0F, 0.0F)
                };
            } catch (Exception e) {
                value = defaultValue;
                ToolLeveling.LOGGER.warn(e.getMessage());
                ToolLeveling.LOGGER.warn("Error while migrating the config value " + getIdentifier() + ", using default value instead");
            }
        } else {
            // deserialize the array-based format
            try {
                JsonArray arr = GsonHelper.getAsJsonArray(json, getIdentifier());
                value = new BonusIngredient[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject obj = GsonHelper.convertToJsonObject(arr.get(i), "[" + i + "]");
                    value[i] = BonusIngredient.deserialize(obj);
                }
            } catch (Exception e) {
                value = defaultValue;
                ToolLeveling.LOGGER.warn(e.getMessage());
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using default value instead");
            }
        }
    }

    @Override
    public BonusIngredient[] get() {
        return value;
    }

}
