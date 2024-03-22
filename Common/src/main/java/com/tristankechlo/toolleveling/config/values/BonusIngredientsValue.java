package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.util.GsonHelper;

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

    @Override
    public BonusIngredient[] get() {
        return value;
    }

}
