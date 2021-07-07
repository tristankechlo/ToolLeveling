package com.tristankechlo.toolleveling.config.values.number;

import com.google.gson.JsonElement;

public final class FloatValue extends NumberValue<Float> {

	public FloatValue(String identifier, Float defaultValue) {
		super(identifier, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE);
	}

	public FloatValue(String identifier, Float defaultValue, Float min, Float max) {
		super(identifier, defaultValue, min, max);
	}

	@Override
	protected boolean verifyValues(Float min, Float max) {
		return min < max;
	}

	@Override
	protected boolean checkInRange(Float check, Float min, Float max) {
		return check >= min && check <= max;
	}

	@Override
	protected Float getAsCast(JsonElement jsonElement) {
		return jsonElement.getAsFloat();
	}

}
