package com.tristankechlo.toolleveling.config.primitives;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfigValue;

public abstract class NumberValue<T extends Number & Comparable<T>> extends AbstractConfigValue<T> {

	protected T value;
	protected final T defaultValue;
	protected final T minValue;
	protected final T maxValue;

	public NumberValue(String identifier, T defaultValue, T min, T max) {
		super(identifier);
		//check if min < max
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("ConfigValue " + identifier + ": min value needs to be lower than the max value");
		}
		//check if defaultValue is in range [min, max]
		if (!checkInRange(defaultValue, min, max)) {
			throw new IllegalArgumentException("ConfigValue " + identifier + ": defaultValue needs to be in range[min|max]");
		}
		this.value = defaultValue;
		this.defaultValue = defaultValue;
		this.minValue = min;
		this.maxValue = max;
	}

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

	abstract T getAsType(JsonElement jsonElement);

	boolean checkInRange(T checkMe, T min, T max) {
		return checkMe.compareTo(min) >= 0 && checkMe.compareTo(max) <= 0;
	}

	@Override
	public void deserialize(JsonObject jsonObject) {
		try {
			JsonElement jsonElement = jsonObject.get(getIdentifier());
			if (jsonElement == null) {
				value = defaultValue;
				ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
				return;
			}
			T checkMe = getAsType(jsonElement);
			if (checkInRange(checkMe, minValue, maxValue)) {
				value = checkMe;
				return;
			}
		} catch (Exception e) {
			ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
		}
		value = defaultValue;
	}

}