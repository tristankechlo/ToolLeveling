package com.tristankechlo.toolleveling.utils;

import net.minecraft.util.Identifier;

public final class Names {

    public static final String MOD_ID = "toolleveling";
    public static final String TABLE = "tool_leveling_table";
    public static final String MOD_NAME = "ToolLeveling";

    public static final class URLS {

        public static final String WIKI = "https://github.com/tristankechlo/ToolLeveling/wiki";
        public static final String CONFIG_START = "https://github.com/tristankechlo/ToolLeveling/wiki/General-Information-to-the-ToolLeveling-Configs";
        public static final String CONFIG_INFO_GENERAL = "https://github.com/tristankechlo/ToolLeveling/wiki/Config-tool_leveling_table.json";
        public static final String CONFIG_INFO_ITEM_VALUES = "https://github.com/tristankechlo/ToolLeveling/wiki/Config-item_values.json";
        public static final String CONFIG_INFO_COMMANDS = "https://github.com/tristankechlo/ToolLeveling/wiki/Config-command_config.json";

    }

    public static final class NetworkChannels {

        public static final Identifier SET_ENCHANTMENT_LEVEL = new Identifier(Names.MOD_ID, "set_enchantment_level");
        public static final Identifier SYNC_CONFIG = new Identifier(Names.MOD_ID, "sync_config");
        public static final Identifier OPEN_ITEMVALUES = new Identifier(Names.MOD_ID, "open_itemvalue_screen");

    }

}
