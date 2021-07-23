package com.tristankechlo.toolleveling.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PaymentSlot extends Slot {

	public PaymentSlot(Container inv, int index, int xPosition, int yPosition) {
		super(inv, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return !stack.isEnchantable() && !stack.isDamageableItem();
	}
}
