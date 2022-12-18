package com.tristankechlo.toolleveling.config.primitives;

import com.google.gson.JsonElement;

public final class DoubleValue extends NumberValue<Double> {

    public DoubleValue(String identifier, Double defaultValue, Double min, Double max) {
        super(identifier, defaultValue, min, max);
    }

    @Override
    Double getAsType(JsonElement jsonElement) {
        return jsonElement.getAsDouble();
    }

}
