package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;

public final class NumberValue<T extends Number & Comparable<T>> extends AbstractConfigValue<T> {

    private T value;
    private final T defaultValue;
    private final T minValue;
    private final T maxValue;
    private final NumberSupplier<T> supplier;

    public NumberValue(String identifier, T defaultValue, T min, T max, NumberSupplier<T> supplier) {
        super(identifier);
        //check if min < max
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("ConfigValue " + identifier + " min value needs to be lower than the max value");
        }
        //check if defaultValue is in range [min, max]
        if (!checkInRange(defaultValue, min, max)) {
            throw new IllegalArgumentException("ConfigValue " + identifier + " defaultValue needs to be in range[min|max]");
        }
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.minValue = min;
        this.maxValue = max;
        this.supplier = supplier;
    }

    @Override
    public void setToDefault() {
        this.value = this.defaultValue;
    }

    @Override
    public void serialize(JsonObject json) {
        json.addProperty(getIdentifier(), value);
    }

    @Override
    public void deserialize(JsonObject json) {
        try {
            T checkMe = this.supplier.getAsType(json, getIdentifier(), defaultValue);
            if (!checkInRange(checkMe, minValue, maxValue)) {
                throw new IllegalArgumentException("ConfigValue " + getIdentifier() + " needs to be in range[" + minValue + "|" + maxValue + "]");
            }
            this.value = checkMe;
        } catch (Exception e) {
            ToolLeveling.LOGGER.warn(e.getMessage());
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
        }
    }

    @Override
    public T get() {
        return this.value;
    }

    private boolean checkInRange(T checkMe, T min, T max) {
        return checkMe.compareTo(min) >= 0 && checkMe.compareTo(max) <= 0;
    }

    @FunctionalInterface
    public interface NumberSupplier<T extends Number & Comparable<T>> {
        T getAsType(JsonObject json, String id, T defaultValue);
    }

}
