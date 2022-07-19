package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.primitives.BooleanValue;

public final class CommandConfig {

    private static final String SUPERENCHANT = "superenchant";
    public static final BooleanValue allowWrongEnchantments = new BooleanValue("allowWrongEnchantments", true);
    public static final BooleanValue allowIncompatibleEnchantments = new BooleanValue("allowIncompatibleEnchantments", true);

    //private static final String TOOLLEVELING = "toolleveling";

    private CommandConfig() {}

    public static void setToDefault() {
        allowWrongEnchantments.setToDefault();
        allowIncompatibleEnchantments.setToDefault();
    }

    public static JsonObject serialize(JsonObject json) {
        JsonObject superenchant = new JsonObject();
        allowWrongEnchantments.serialize(superenchant);
        allowIncompatibleEnchantments.serialize(superenchant);
        json.add(SUPERENCHANT, superenchant);
        //json.add(TOOLLEVELING, new JsonObject());
        return json;
    }

    public static void deserialize(JsonObject json) {
        if (json.has(SUPERENCHANT)) {
            JsonObject superenchant = json.get(SUPERENCHANT).getAsJsonObject();
            allowWrongEnchantments.deserialize(superenchant);
            allowIncompatibleEnchantments.deserialize(superenchant);
        }
    }

}
