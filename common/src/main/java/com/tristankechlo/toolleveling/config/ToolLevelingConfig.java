package com.tristankechlo.toolleveling.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.config.options.CommandOptions;
import com.tristankechlo.toolleveling.config.options.EnchantmentOptions;
import com.tristankechlo.toolleveling.config.options.GeneralOptions;

public record ToolLevelingConfig(
        GeneralOptions generalOptions,
        EnchantmentOptions enchantmentOptions,
        CommandOptions commandOptions
) {

    public static final Codec<ToolLevelingConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    GeneralOptions.CODEC.fieldOf("general_options").forGetter(ToolLevelingConfig::generalOptions),
                    EnchantmentOptions.CODEC.fieldOf("enchantment_options").forGetter(ToolLevelingConfig::enchantmentOptions),
                    CommandOptions.CODEC.fieldOf("command_options").forGetter(ToolLevelingConfig::commandOptions)
            ).apply(instance, ToolLevelingConfig::new)
    );

    private static final ToolLevelingConfig DEFAULT = new ToolLevelingConfig(
            GeneralOptions.DEFAULT,
            EnchantmentOptions.DEFAULT,
            CommandOptions.DEFAULT
    );

    private static ToolLevelingConfig INSTANCE = DEFAULT;

    public static ToolLevelingConfig get() {
        return INSTANCE;
    }

    public static void set(ToolLevelingConfig config) {
        INSTANCE = config;
    }

    public static void setToDefault() {
        INSTANCE = ToolLevelingConfig.DEFAULT;
    }

}
