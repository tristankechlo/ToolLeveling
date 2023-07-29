package com.tristankechlo.toolleveling.config;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public final class ToolLevelingConfig {

    // bounds for the success chance of the enchantment process
    public static final Supplier<Float> minSuccessChance = () -> 75.0F; // 0 - 100
    public static final Supplier<Float> maxSuccessChance = () -> 100.0F; // 0 - 100

    // what amount of bookshelves are required for 100% success chance
    public static final Supplier<Integer> requiredBookshelves = () -> 20; // 0 - 32

    // how many books are required to start the enchantment process
    public static final Supplier<Integer> requiredBooks = () -> 4; // 1 - 8

    public static boolean isBonusItemStrength(ItemStack stack) {
        return stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    public static boolean isBonusItemIterations(ItemStack stack) {
        return stack.is(Items.NETHER_STAR);
    }

}
