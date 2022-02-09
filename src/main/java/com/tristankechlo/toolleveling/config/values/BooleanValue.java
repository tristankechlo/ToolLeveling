package com.tristankechlo.toolleveling.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;

public final class BooleanValue extends AbstractConfigValue<Boolean> {

	private boolean value;
	private final boolean defaultValue;

	public BooleanValue(String identifier) {
		this(identifier, true);
	}

	public BooleanValue(String identifier, boolean defaultValue) {
		super(identifier);
		this.value = defaultValue;
		this.defaultValue = defaultValue;
	}

	@Override
	public void setToDefault() {
		value = defaultValue;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void serialize(JsonObject jsonObject) {
		jsonObject.addProperty(getIdentifier(), value);
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		try {
			value = jsonObject.get(getIdentifier()).getAsBoolean();
		} catch (Exception e) {
			value = defaultValue;
			ToolLeveling.LOGGER
					.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
		}
	}

}
