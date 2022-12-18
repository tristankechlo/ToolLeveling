package com.tristankechlo.toolleveling.config.primitives;

import com.google.gson.JsonElement;

public final class LongValue extends NumberValue<Long> {

    public LongValue(String name, Long defaultValue, Long minValue, Long maxValue) {
        super(name, defaultValue, minValue, maxValue);
    }

    @Override
    Long getAsType(JsonElement jsonElement) {
        return jsonElement.getAsLong();
    }

}
