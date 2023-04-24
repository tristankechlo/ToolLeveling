package com.tristankechlo.toolleveling.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public final class ToolLevelingConfig {

    public static final Supplier<Float> MIN_SUCCESS_CHANCE = () -> 0.5F;
    public static final Supplier<Float> MAX_SUCCESS_CHANCE = () -> 1.0F;
    public static final Supplier<Item> PAYMENT_ITEM = () -> Items.LAPIS_LAZULI;

}
