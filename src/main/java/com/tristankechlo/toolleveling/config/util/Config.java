package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Config {

    private final String fileName;
    private final Runnable reset;
    private final Function<JsonObject, JsonObject> serializer;
    private final Consumer<JsonObject> deserializer;

    public Config(String fileName, Runnable reset, Function<JsonObject, JsonObject> serializer, Consumer<JsonObject> deserializer) {
        this.fileName = fileName;
        this.reset = reset;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setToDefault() {
        this.reset.run();
    }

    public JsonObject serialize(JsonObject json) {
        return this.serializer.apply(json);
    }

    public void deserialize(JsonObject json) {
        this.deserializer.accept(json);
    }

}