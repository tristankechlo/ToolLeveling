package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.util.StringIdentifiable;

public enum ConfigIdentifier implements StringIdentifiable {

	GENERAL("general"),
	ITEMVALUES("item_values");

	@SuppressWarnings("deprecation")
	public static final Codec<ConfigIdentifier> CODEC;
	private final String name;

	private ConfigIdentifier(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return this.toString();
	}

	public String withModID() {
		return Names.MOD_ID + ":" + this.name;
	}

	static {
		CODEC = StringIdentifiable.createCodec(ConfigIdentifier::values);
	}

}
