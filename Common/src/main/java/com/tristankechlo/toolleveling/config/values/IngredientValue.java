package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public final class IngredientValue extends AbstractConfigValue<Ingredient> {

    private Ingredient value;
    private final Ingredient defaultValue;
    private final String comment;

    public IngredientValue(String name, Ingredient defaultValue, String comment) {
        super(name);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    @Override
    public void setToDefault() {
        this.value = defaultValue;
    }

    @Override
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        if (comment != null && !comment.isEmpty()) {
            jsonObject.addProperty("__comment", comment);
        }
        jsonObject.add("value", value.toJson());
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            value = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "value"));
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
