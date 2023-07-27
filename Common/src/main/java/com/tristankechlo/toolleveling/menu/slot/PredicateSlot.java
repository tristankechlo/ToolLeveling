package com.tristankechlo.toolleveling.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class PredicateSlot extends Slot {

    private final Predicate<ItemStack> predicate;
    private final int maxStackSize;

    public PredicateSlot(Container inv, int index, int xPosition, int yPosition, Predicate<ItemStack> predicate) {
        this(inv, index, xPosition, yPosition, predicate, -1);
    }

    public PredicateSlot(Container inv, int index, int xPosition, int yPosition, Predicate<ItemStack> predicate, int maxStackSize) {
        super(inv, index, xPosition, yPosition);
        this.predicate = predicate;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.predicate.test(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack $$0) {
        if (this.maxStackSize > 0) {
            return this.maxStackSize;
        }
        return super.getMaxStackSize($$0);
    }

    @Override
    public int getMaxStackSize() {
        if (this.maxStackSize > 0) {
            return this.maxStackSize;
        }
        return super.getMaxStackSize();
    }

}
