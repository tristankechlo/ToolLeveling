package com.tristankechlo.toolleveling.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.config.options.SuperEnchantOptions;

public record CommandConfig(SuperEnchantOptions superEnchantOptions) {

    public static final Codec<CommandConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SuperEnchantOptions.CODEC.fieldOf("superenchant").forGetter(CommandConfig::superEnchantOptions)
            ).apply(instance, CommandConfig::new)
    );

    public static final CommandConfig DEFAULT = new CommandConfig(SuperEnchantOptions.DEFAULT);

    private static CommandConfig INSTANCE = DEFAULT;

    public static CommandConfig get() {
        return INSTANCE;
    }

    public static void set(CommandConfig config) {
        INSTANCE = config;
    }

    public static void setToDefault() {
        INSTANCE = CommandConfig.DEFAULT;
    }

}
