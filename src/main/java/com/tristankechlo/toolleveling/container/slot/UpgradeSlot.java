package com.tristankechlo.toolleveling.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class UpgradeSlot extends Slot {

    public UpgradeSlot(IInventory inv, int index, int xPosition, int yPosition) {
        super(inv, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.isEnchanted();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
