package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.util.StringIdentifiable;

public enum ConfigIdentifier implements StringIdentifiable {

	GENERAL,
	ITEMVALUES;

	@SuppressWarnings("deprecation")
	public static final Codec<ConfigIdentifier> CODEC;

	@Override
	public String asString() {
		return this.toString();
	}

	public String withModID() {
		return Names.MOD_ID + ":" + this.toString().toLowerCase();
	}

	static {
		CODEC = StringIdentifiable.createCodec(ConfigIdentifier::values);
	}

}
