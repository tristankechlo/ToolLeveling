package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.util.GsonHelper;

public final class BooleanValue extends AbstractConfigValue<Boolean> {

    private boolean value;
    private final boolean defaultValue;

    public BooleanValue(String identifier, boolean defaultValue) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @Override
    public void setToDefault() {
        this.value = defaultValue;
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void serialize(JsonObject json) {
        json.addProperty(getIdentifier(), value);
    }

    @Override
    public void deserialize(JsonObject json) {
        try {
            value = GsonHelper.getAsBoolean(json, getIdentifier(), defaultValue);
        } catch (Exception e) {
            value = defaultValue;
            ToolLeveling.LOGGER.warn(e.getMessage());
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using default value instead");
        }
    }

}
