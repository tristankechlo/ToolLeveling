package com.tristankechlo.toolleveling.config.primitives;

import com.google.gson.JsonElement;

public class DoubleValue extends NumberValue<Double> {

    public DoubleValue(String name, Double defaultValue, Double min, Double max) {
        super(name, defaultValue, min, max);
    }

    @Override
    Double getAsType(JsonElement jsonElement) {
        return jsonElement.getAsDouble();
    }

}
