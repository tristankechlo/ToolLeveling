package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public final class Predicates {

    public static final Predicate<ItemStack> IS_BOOK = (stack) -> stack.is(Items.ENCHANTED_BOOK); // purple slots
    public static final Predicate<ItemStack> IS_UPGRADE_ITEM = (stack) -> !IS_BOOK.test(stack) && (stack.isEnchanted() || stack.isEnchantable()); // red slot

    public static final Predicate<ItemStack> BONUS_ITEM_STRENGTH = ToolLevelingConfig.INSTANCE::isBonusItemStrength;
    public static final Predicate<ItemStack> BONUS_ITEM_ITERATIONS = ToolLevelingConfig.INSTANCE::isBonusItemIterations;
    public static final Predicate<ItemStack> IS_BONUS_ITEM = (stack) -> BONUS_ITEM_STRENGTH.test(stack) || BONUS_ITEM_ITERATIONS.test(stack); // green slots

}
