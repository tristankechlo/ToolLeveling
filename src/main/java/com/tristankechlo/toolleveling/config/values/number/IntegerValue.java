package com.tristankechlo.toolleveling.config.values.number;

import com.google.gson.JsonElement;

public final class IntegerValue extends NumberValue<Integer> {

	public IntegerValue(String identifier, Integer defaultValue) {
		super(identifier, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public IntegerValue(String identifier, Integer defaultValue, Integer min, Integer max) {
		super(identifier, defaultValue, min, max);
	}

	@Override
	protected boolean verifyValues(Integer min, Integer max) {
		return min < max;
	}

	@Override
	protected boolean checkInRange(Integer check, Integer min, Integer max) {
		return check >= min && check <= max;
	}

	@Override
	protected Integer getAsCast(JsonElement jsonElement) {
		return jsonElement.getAsInt();
	}

}
