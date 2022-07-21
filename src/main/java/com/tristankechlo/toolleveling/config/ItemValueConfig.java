package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.primitives.LongValue;
import com.tristankechlo.toolleveling.config.values.ItemValues;

public final class ItemValueConfig {

    public static final LongValue defaultItemWorth = new LongValue("default_item_worth", 10L, 0L, Long.MAX_VALUE);
    public static final ItemValues itemValues = new ItemValues("item_values");

    private ItemValueConfig() {}

    public static JsonObject serialize(JsonObject json) {
        defaultItemWorth.serialize(json);
        itemValues.serialize(json);
        return json;
    }

    public static void deserialize(JsonObject json) {
        defaultItemWorth.deserialize(json);
        itemValues.deserialize(json);
    }

    public static void setToDefault() {
        defaultItemWorth.setToDefault();
        itemValues.setToDefault();
    }

}
