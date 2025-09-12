package com.tristankechlo.toolleveling.config.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CommandOptions(SuperEnchantOptions superEnchantOptions) {

    public static final Codec<CommandOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SuperEnchantOptions.CODEC.fieldOf("superenchant").forGetter(CommandOptions::superEnchantOptions)
            ).apply(instance, CommandOptions::new)
    );

    public static final CommandOptions DEFAULT = new CommandOptions(SuperEnchantOptions.DEFAULT);

}
