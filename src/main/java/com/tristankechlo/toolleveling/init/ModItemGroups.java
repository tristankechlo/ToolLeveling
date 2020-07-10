package com.tristankechlo.toolleveling.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModItemGroups {

    public static final ItemGroup General = new ItemGroup("ToolLevelingTab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.EXPERIENCE_BOTTLE);
		}
	};
}
