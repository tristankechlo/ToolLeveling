package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.values.AbstractConfigValue;

import java.util.List;

public abstract class AbstractConfig {

    private final String fileName;
    private final String infoUrl;

    public AbstractConfig(String fileName, String infoUrl) {
        this.fileName = fileName;
        this.infoUrl = infoUrl;
    }

    public void setToDefault() {
        this.getValues().forEach(AbstractConfigValue::setToDefault);
    }

    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("__comment", this.getComment());
        this.getValues().forEach(value -> value.serialize(jsonObject));
        return jsonObject;
    }

    public void deserialize(JsonObject json) {
        this.getValues().forEach(value -> value.deserialize(json));
    }

    public String getFileName() {
        return fileName;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    protected abstract List<AbstractConfigValue<?>> getValues();

    protected abstract String getComment();

}
