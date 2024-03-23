package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record BonusIngredient(Ingredient ingredient, float minLevelBonus, float maxLevelBonus, float iterationsBonus) {

    public BonusIngredient {
        if (ingredient == null) {
            throw new NullPointerException("ingredient of the bonus ingredient can't be null");
        }
        if (minLevelBonus == 0 && maxLevelBonus == 0 && iterationsBonus == 0) {
            throw new NullPointerException("bonus ingredient must provide at least one bonus");
        }
    }

    public static void serialize(BonusIngredient value, JsonObject json) {
        json.add("ingredient", value.ingredient().toJson());
        json.addProperty("min_level_bonus", value.minLevelBonus());
        json.addProperty("max_level_bonus", value.maxLevelBonus());
        json.addProperty("iterations_bonus", value.iterationsBonus());
    }

    public static BonusIngredient deserialize(JsonObject json) {
        JsonObject obj = GsonHelper.getAsJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(obj);
        float minLevelBonus = GsonHelper.getAsFloat(json, "min_level_bonus", 0.0F);
        float maxLevelBonus = GsonHelper.getAsFloat(json, "max_level_bonus", 0.0F);
        float iterationsBonus = GsonHelper.getAsFloat(json, "iterations_bonus", 0.0F);
        return new BonusIngredient(ingredient, minLevelBonus, maxLevelBonus, iterationsBonus);
    }

}
