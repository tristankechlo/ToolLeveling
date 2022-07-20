package com.tristankechlo.toolleveling.utils;

import net.minecraft.util.Identifier;

public final class Names {

    public static final String MOD_ID = "toolleveling";
    public static final String TABLE = "tool_leveling_table";
    public static final String MOD_NAME = "ToolLeveling";

    public static final class URLs {

        public static final String CONFIG_START = "https://gist.github.com/tristankechlo/a7f826df8d5fecbe3fe187f1c13a2af4";
        public static final String CONFIG_INFO_GENERAL = "https://gist.github.com/tristankechlo/3818a20804ba69809422de42bf51ee81";
        public static final String CONFIG_INFO_ITEM_VALUES = "https://gist.github.com/tristankechlo/31903eaca2c3f05cde7223cb2c19b416";
        public static final String CONFIG_INFO_COMMANDS = "https://gist.github.com/tristankechlo/246643b3c94d8ad4aabfc37165a891d7";

    }

    public static final class NetworkChannels {

        public static final Identifier SET_ENCHANTMENT_LEVEL = new Identifier(Names.MOD_ID, "set_enchantment_level");
        public static final Identifier SYNC_CONFIG = new Identifier(Names.MOD_ID, "sync_config");
        public static final Identifier OPEN_ITEMVALUES = new Identifier(Names.MOD_ID, "open_itemvalue_screen");

    }

}
