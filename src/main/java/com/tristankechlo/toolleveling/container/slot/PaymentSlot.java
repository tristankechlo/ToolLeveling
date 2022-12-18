package com.tristankechlo.toolleveling.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class PaymentSlot extends Slot {

    public PaymentSlot(IInventory inv, int index, int xPosition, int yPosition) {
        super(inv, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !stack.isEnchantable() && !stack.isDamageableItem();
    }
}
