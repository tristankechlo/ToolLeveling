package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.util.GsonHelper;

public final class BooleanValue extends AbstractConfigValue<Boolean> {

    private boolean value;
    private final boolean defaultValue;
    private final String comment;

    public BooleanValue(String identifier, boolean defaultValue, String comment) {
        super(identifier);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.comment = comment;
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
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        if (comment != null && !comment.isEmpty()) {
            jsonObject.addProperty("__comment", comment);
        }
        jsonObject.addProperty("value", value);
        return jsonObject;
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            value = GsonHelper.getAsBoolean(jsonObject, "value", defaultValue);
        } catch (Exception e) {
            value = defaultValue;
            ToolLeveling.LOGGER.warn(e.getMessage());
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using default value instead");
        }
    }

}
