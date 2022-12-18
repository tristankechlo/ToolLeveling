package com.tristankechlo.toolleveling.config.util;

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonObject;

public final class ConfigHandler {

	private String fileName;
	private Runnable reset;
	private Function<JsonObject, JsonObject> serializer;
	private Consumer<JsonObject> deserializer;

	public ConfigHandler(String fileName, Runnable reset, Function<JsonObject, JsonObject> serializer,
			Consumer<JsonObject> deserializer) {
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
