package com.tristankechlo.toolleveling.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public final class ToolLevelingConfig {

    public static final Supplier<Float> minSuccessChance = () -> 75.0F;
    public static final Supplier<Float> maxSuccessChance = () -> 95.0F;
    private static final Supplier<Item> paymentItem = () -> Items.LAPIS_LAZULI;

    public static boolean isPaymentItem(ItemStack stack) {
        return stack.is(paymentItem.get());
    }

    public static boolean isBonusItemHigherLevel(ItemStack stack) {
        return stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    public static boolean isBonusItemExtraEnchantment(ItemStack stack) {
        return stack.is(Items.NETHER_STAR);
    }

}
