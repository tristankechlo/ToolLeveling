package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;

import java.util.function.Function;

public final class NumberValue<T extends Number & Comparable<T>> extends AbstractConfigValue<T> {

    private T value;
    private final T defaultValue;
    private final T minValue;
    private final T maxValue;
    private final String comment;
    private final Function<JsonElement, T> getAsType;

    public NumberValue(String identifier, T defaultValue, T min, T max, Function<JsonElement, T> getAsType, String comment) {
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
        this.comment = comment;
        this.getAsType = getAsType;
    }

    @Override
    public void setToDefault() {
        this.value = this.defaultValue;
    }

    @Override
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        if (this.comment != null && !this.comment.isEmpty()) {
            jsonObject.addProperty("__comment", this.comment);
        }
        jsonObject.addProperty("__possibleValues", "range[" + this.minValue + "|" + this.maxValue + "]");
        jsonObject.addProperty("value", this.value);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            JsonElement jsonElement = jsonObject.get("value");
            if (jsonElement == null) {
                value = defaultValue;
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
                return;
            }
            T checkMe = getAsType.apply(jsonElement);
            if (checkInRange(checkMe, minValue, maxValue)) {
                value = checkMe;
                return;
            }
        } catch (Exception e) {
            ToolLeveling.LOGGER.warn(e.getMessage());
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
        }
        value = defaultValue;
    }

    @Override
    public T get() {
        return this.value;
    }

    private boolean checkInRange(T checkMe, T min, T max) {
        return checkMe.compareTo(min) >= 0 && checkMe.compareTo(max) <= 0;
    }

}
