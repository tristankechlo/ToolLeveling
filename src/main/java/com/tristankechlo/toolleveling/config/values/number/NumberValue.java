package com.tristankechlo.toolleveling.config.values.number;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.values.AbstractConfigValue;

public abstract class NumberValue<T extends Number> extends AbstractConfigValue<T> {

	protected T value;
	protected final T defaultValue;
	protected final T minValue;
	protected final T maxValue;

	public NumberValue(String identifier, T defaultValue, T min, T max) {
		super(identifier);
		if (!verifyValues(min, max)) {
			throw new IllegalArgumentException(
					"ConfigValue " + identifier + ": min value needs to be lower than the max value");
		}
		if (!checkInRange(defaultValue, min, max)) {
			throw new IllegalArgumentException(
					"ConfigValue " + identifier + ": defaultValue needs to be in range[min|max]");
		}
		this.value = defaultValue;
		this.defaultValue = defaultValue;
		this.minValue = min;
		this.maxValue = max;
	}

	/** check if min is lower than max */
	protected abstract boolean verifyValues(T min, T max);

	/** check if a given value is in range [min|max] */
	protected abstract boolean checkInRange(T check, T min, T max);

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
				ToolLeveling.LOGGER.warn(
						"Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
				return;
			}
			T checkMe = getAsCast(jsonElement);
			if (checkInRange(checkMe, minValue, maxValue)) {
				value = checkMe;
				return;
			}
		} catch (Exception e) {
			ToolLeveling.LOGGER
					.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
		}
		value = defaultValue;
	}

	protected abstract T getAsCast(JsonElement jsonElement);

}
