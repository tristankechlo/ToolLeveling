package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;

import java.util.function.Supplier;

public abstract class AbstractConfigValue<T> implements Supplier<T> {

    private final String identifier;

    public AbstractConfigValue(String identifier) {
        if (identifier == null) {
            throw new NullPointerException("identifier of the config value can't be null");
        }
        if (identifier.length() <= 1) {
            throw new IllegalArgumentException("identifier of the config value must be longer than 1 char");
        }
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public abstract void setToDefault();

    public abstract JsonObject serialize();

    public abstract void deserialize(JsonObject jsonObject);

}
