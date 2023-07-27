package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public final class Predicates {

    public static final Predicate<ItemStack> BOOK = (stack) -> stack.is(Items.ENCHANTED_BOOK); // purple slots
    public static final Predicate<ItemStack> PAYMENT = ToolLevelingConfig::isPaymentItem; // blue slots
    public static final Predicate<ItemStack> UPGRADE = (stack) -> !BOOK.test(stack) && (stack.isEnchanted() || stack.isEnchantable()); // red slot

    private static final Predicate<ItemStack> ITEM_HIGHER_LEVEL = ToolLevelingConfig::isBonusItemHigherLevel;
    private static final Predicate<ItemStack> ITEM_EXTRA_ENCHANTMENT = ToolLevelingConfig::isBonusItemExtraEnchantment;
    public static final Predicate<ItemStack> BONUS = (stack) -> ITEM_HIGHER_LEVEL.test(stack) || ITEM_EXTRA_ENCHANTMENT.test(stack); // green slots

}
