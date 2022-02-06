package com.tristankechlo.toolleveling.screenhandler.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class UpgradeSlot extends Slot {

	public UpgradeSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return stack.hasEnchantments();
	}

	@Override
	public int getMaxItemCount() {
		return 1;
	}

}
