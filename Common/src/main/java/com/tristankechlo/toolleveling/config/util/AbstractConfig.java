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
        this.getValues().forEach(value -> jsonObject.add(value.getIdentifier(), value.serialize()));
        return jsonObject;
    }

    public void deserialize(JsonObject jsonObject) {
        this.getValues().forEach(value -> {
            if (jsonObject.has(value.getIdentifier())) {
                value.deserialize(jsonObject.getAsJsonObject(value.getIdentifier()));
            }
        });
    }

    public String getFileName() {
        return fileName;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    protected abstract List<AbstractConfigValue<?>> getValues();

}
