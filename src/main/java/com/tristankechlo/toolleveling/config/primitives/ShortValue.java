package com.tristankechlo.toolleveling.config.primitives;

import com.google.gson.JsonElement;

public class ShortValue extends NumberValue<Short> {

	public ShortValue(String name, Short defaultValue, Short minValue, Short maxValue) {
		super(name, defaultValue, minValue, maxValue);
	}

	@Override
	Short getAsType(JsonElement jsonElement) {
		return jsonElement.getAsShort();
	}

}
