package com.tristankechlo.toolleveling.config.values.number;

import com.google.gson.JsonElement;

public final class ShortValue extends NumberValue<Short> {

	public ShortValue(String identifier, Short defaultValue) {
		super(identifier, defaultValue, Short.MIN_VALUE, Short.MAX_VALUE);
	}

	public ShortValue(String identifier, Short defaultValue, Short min, Short max) {
		super(identifier, defaultValue, min, max);
	}

	@Override
	protected boolean verifyValues(Short min, Short max) {
		return min < max;
	}

	@Override
	protected boolean checkInRange(Short check, Short min, Short max) {
		return check >= min && check <= max;
	}

	@Override
	protected Short getAsCast(JsonElement jsonElement) {
		return jsonElement.getAsShort();
	}

}
