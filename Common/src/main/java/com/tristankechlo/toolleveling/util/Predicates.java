package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public final class Predicates {

    public static final Predicate<ItemStack> BOOK = (stack) -> stack.is(Items.ENCHANTED_BOOK);
    public static final Predicate<ItemStack> PAYMENT = (stack) -> stack.is(ToolLevelingConfig.PAYMENT_ITEM.get());
    public static final Predicate<ItemStack> UPGRADE = ItemStack::isEnchanted;

}
