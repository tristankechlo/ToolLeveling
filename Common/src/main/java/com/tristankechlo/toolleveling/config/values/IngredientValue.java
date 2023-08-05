package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public final class IngredientValue extends AbstractConfigValue<Ingredient> {

    private Ingredient value;
    private final Ingredient defaultValue;

    public IngredientValue(String name, Ingredient defaultValue) {
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
        json.add(getIdentifier(), value.toJson());
    }

    @Override
    public void deserialize(JsonObject json) {
        try {
            value = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, getIdentifier()));
        } catch (Exception e) {
            value = defaultValue;
            ToolLeveling.LOGGER.warn(e.getMessage());
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using default value instead");
        }
    }

    @Override
    public Ingredient get() {
        return value;
    }

}
