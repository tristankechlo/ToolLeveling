package com.tristankechlo.toolleveling.items;

import com.tristankechlo.toolleveling.init.ModItemGroups;

import net.minecraft.item.Item;

public class RubyItem extends Item{

	public RubyItem() {
		super(
			new Item.Properties().group(ModItemGroups.General).maxStackSize(16)
		);
	}
}
