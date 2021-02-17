package com.tristankechlo.toolleveling.config.misc;

import com.google.gson.JsonObject;

public final class ConfigHelper {

	private ConfigHelper() {
	}

	public static int getInRange(JsonObject json, String name, int min, int max, int defaultValue) {
		try {
			int checkMe = json.get(name).getAsInt();
			if (checkMe >= min && checkMe <= max) {
				return checkMe;
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	public static double getInRange(JsonObject json, String name, double min, double max, double defaultValue) {
		try {
			double checkMe = json.get(name).getAsDouble();
			if (checkMe >= min && checkMe <= max) {
				return checkMe;
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	public static boolean getOrDefault(JsonObject json, String name, boolean defaultValue) {
		try {
			boolean result = json.get(name).getAsBoolean();
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int checkRange(int checkMe, int min, int max, int def) {
		if (checkMe >= min && checkMe <= max) {
			return checkMe;
		}
		return def;
	}
}
