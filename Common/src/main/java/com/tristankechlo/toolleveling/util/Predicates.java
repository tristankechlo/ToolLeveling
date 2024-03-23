package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class Predicates {

    public static final Predicate<ItemStack> IS_BOOK = (stack) -> stack.is(Items.ENCHANTED_BOOK); // purple slots
    public static final Predicate<ItemStack> IS_UPGRADE_ITEM = (stack) -> !IS_BOOK.test(stack) && (stack.isEnchanted() || stack.isEnchantable()); // red slot

    public static final IntSupplier BASE_MIN_STRENGTH_VAL = ToolLevelingConfig.INSTANCE::getBaseMinStrength;
    public static final ToIntFunction<ItemStack> BONUS_ITEM_MIN_STRENGTH_VAL = ToolLevelingConfig.INSTANCE::getBonusItemMinStrength;
    public static final Predicate<ItemStack> BONUS_ITEM_MIN_STRENGTH = (stack) -> BONUS_ITEM_MIN_STRENGTH_VAL.applyAsInt(stack) != 0;
    public static final IntSupplier BASE_STRENGTH_VAL = ToolLevelingConfig.INSTANCE::getBaseStrength;
    public static final ToIntFunction<ItemStack> BONUS_ITEM_STRENGTH_VAL = ToolLevelingConfig.INSTANCE::getBonusItemStrength;
    public static final Predicate<ItemStack> BONUS_ITEM_STRENGTH = (stack) -> BONUS_ITEM_STRENGTH_VAL.applyAsInt(stack) != 0;
    public static final IntSupplier BASE_ITERATIONS_VAL= ToolLevelingConfig.INSTANCE::getBaseIterations;
    public static final ToIntFunction<ItemStack> BONUS_ITEM_ITERATIONS_VAL = ToolLevelingConfig.INSTANCE::getBonusItemIterations;
    public static final Predicate<ItemStack> BONUS_ITEM_ITERATIONS = (stack) -> BONUS_ITEM_ITERATIONS_VAL.applyAsInt(stack) != 0;
    public static final Predicate<ItemStack> IS_BONUS_ITEM = (stack) -> BONUS_ITEM_MIN_STRENGTH.test(stack) || BONUS_ITEM_STRENGTH.test(stack) || BONUS_ITEM_ITERATIONS.test(stack); // green slots

}
