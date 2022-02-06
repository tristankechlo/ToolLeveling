package com.tristankechlo.toolleveling.screenhandler.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class PaymentSlot extends Slot {

	public PaymentSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return !stack.isEnchantable() && !stack.isDamageable();
	}

}
