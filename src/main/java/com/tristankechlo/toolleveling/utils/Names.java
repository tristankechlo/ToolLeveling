package com.tristankechlo.toolleveling.utils;

import net.minecraft.util.Identifier;

public final class Names {

	public static final String MOD_ID = "toolleveling";
	public static final String TABLE = "tool_leveling_table";

	public static class NetworkChannels {

		public static final Identifier SET_ENCHANTMENT_LEVEL = new Identifier(Names.MOD_ID, "set_enchantment_level");
		public static final Identifier SYNC_CONFIG = new Identifier(Names.MOD_ID, "sync_config");
		public static final Identifier OPEN_ITEMVALUES = new Identifier(Names.MOD_ID, "open_itemvalue_screen");

	}

}
