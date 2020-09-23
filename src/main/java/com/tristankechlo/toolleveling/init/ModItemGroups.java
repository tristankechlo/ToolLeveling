package com.tristankechlo.toolleveling.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {

    public static final ItemGroup General = new ItemGroup("ToolLevelingTab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.TOOL_LEVELING_TABLE_ITEM.get());
		}
	};
}
