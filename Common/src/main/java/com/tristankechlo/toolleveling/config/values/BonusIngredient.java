package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record BonusIngredient(Ingredient ingredient, int maxLevelBonus, int iterationsBonus) {

    public BonusIngredient {
        if (ingredient == null) {
            throw new NullPointerException("ingredient of the bonus ingredient can't be null");
        }
        if (maxLevelBonus == 0 && iterationsBonus == 0) {
            throw new NullPointerException("bonus ingredient must provide at least one bonus");
        }
    }

    public static void serialize(BonusIngredient value, JsonObject json) {
        json.add("ingredient", value.ingredient().toJson());
        json.addProperty("max_level_bonus", value.maxLevelBonus());
        json.addProperty("iterations_bonus", value.iterationsBonus());
    }

    public static BonusIngredient deserialize(JsonObject json) {
        JsonObject obj = GsonHelper.getAsJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(obj);
        int maxLevelBonus = GsonHelper.getAsInt(json, "max_level_bonus", 0);
        int iterationsBonus = GsonHelper.getAsInt(json, "iterations_bonus", 0);
        return new BonusIngredient(ingredient, maxLevelBonus, iterationsBonus);
    }

}
