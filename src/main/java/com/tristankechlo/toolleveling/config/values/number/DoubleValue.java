package com.tristankechlo.toolleveling.config.values.number;

import com.google.gson.JsonElement;

public final class DoubleValue extends NumberValue<Double> {

	public DoubleValue(String identifier, Double defaultValue) {
		super(identifier, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
	}

	public DoubleValue(String identifier, Double defaultValue, Double min, Double max) {
		super(identifier, defaultValue, min, max);
	}

	@Override
	protected boolean verifyValues(Double min, Double max) {
		return min < max;
	}

	@Override
	protected boolean checkInRange(Double check, Double min, Double max) {
		return check >= min && check <= max;
	}

	@Override
	protected Double getAsCast(JsonElement jsonElement) {
		return jsonElement.getAsDouble();
	}

}
