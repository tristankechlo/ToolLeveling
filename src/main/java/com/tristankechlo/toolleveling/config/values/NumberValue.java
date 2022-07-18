package com.tristankechlo.toolleveling.config.values;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.values.AbstractConfigValue;

import java.lang.reflect.Type;

public class NumberValue<T extends Number & Comparable<T>> extends AbstractConfigValue<T> {

    protected T value;
    protected final T defaultValue;
    protected final T minValue;
    protected final T maxValue;
    private static final Gson GSON = new Gson();
    private final Type TYPE = new TypeToken<T>() {}.getType();

    public NumberValue(String identifier, T defaultValue, T min, T max) {
        super(identifier);
        //check if min < max
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("ConfigValue " + identifier + ": min value needs to be lower than the max value");
        }
        //check if defaultValue is in range [min, max]
        if (!checkInRange(defaultValue, min, max)) {
            throw new IllegalArgumentException("ConfigValue " + identifier + ": defaultValue needs to be in range[min|max]");
        }
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.minValue = min;
        this.maxValue = max;
    }

    /**
     * check if a given value is in range [min|max]
     */
    protected boolean checkInRange(T check, T min, T max) {
        return check.compareTo(min) >= 0 && check.compareTo(max) <= 0;
    }

    @Override
    public void setToDefault() {
        value = defaultValue;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(getIdentifier(), getValue());
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            JsonElement jsonElement = jsonObject.get(getIdentifier());
            if (jsonElement == null) {
                value = defaultValue;
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
                return;
            }
            T checkMe = getAsCast(jsonElement);
            if (checkInRange(checkMe, minValue, maxValue)) {
                value = checkMe;
                return;
            }
        } catch (Exception e) {
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
        }
        value = defaultValue;
    }

    protected T getAsCast(JsonElement jsonElement) {
        return GSON.fromJson(jsonElement, TYPE);
    }

}