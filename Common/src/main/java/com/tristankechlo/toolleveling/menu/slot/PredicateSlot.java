package com.tristankechlo.toolleveling.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class PredicateSlot extends Slot {

    private final Predicate<ItemStack> predicate;

    public PredicateSlot(Container inv, int index, int xPosition, int yPosition, Predicate<ItemStack> predicate) {
        super(inv, index, xPosition, yPosition);
        this.predicate = predicate;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.predicate.test(stack);
    }

}
