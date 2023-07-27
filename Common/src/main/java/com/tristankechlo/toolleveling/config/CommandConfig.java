package com.tristankechlo.toolleveling.config;

import java.util.function.Supplier;

public final class CommandConfig {

    public static final Supplier<Boolean> allowWrongEnchantments = () -> true;
    public static final Supplier<Boolean> allowIncompatibleEnchantments = () -> true;

}
