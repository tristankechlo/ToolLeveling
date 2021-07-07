package com.tristankechlo.toolleveling.config.values.number;

import com.google.gson.JsonElement;

public final class LongValue extends NumberValue<Long> {

	public LongValue(String identifier, long defaultValue) {
		super(identifier, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public LongValue(String identifier, long defaultValue, long min, long max) {
		super(identifier, defaultValue, min, max);
	}

	@Override
	protected boolean verifyValues(Long min, Long max) {
		return min < max;
	}

	@Override
	protected boolean checkInRange(Long check, Long min, Long max) {
		return check >= min && check <= max;
	}

	@Override
	protected Long getAsCast(JsonElement jsonElement) {
		return jsonElement.getAsLong();
	}

}
