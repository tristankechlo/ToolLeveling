package com.tristankechlo.toolleveling.menu.slot;

import com.tristankechlo.toolleveling.util.Predicates;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public final class UpgradeSlot extends PredicateSlot {

    public UpgradeSlot(Container inv, int index, int xPosition, int yPosition) {
        super(inv, index, xPosition, yPosition, Predicates.IS_UPGRADE_ITEM);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack $$0) {
        return 1;
    }

}
