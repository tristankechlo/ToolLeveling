package com.tristankechlo.toolleveling.config.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SuperEnchantOptions(boolean allowWrongEnchantments, boolean allowIncompatibleEnchantments) {

    public static final Codec<SuperEnchantOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("allow_wrong_enchantments").forGetter(SuperEnchantOptions::allowWrongEnchantments),
                    Codec.BOOL.fieldOf("allow_incompatible_enchantments").forGetter(SuperEnchantOptions::allowIncompatibleEnchantments)
            ).apply(instance, SuperEnchantOptions::new)
    );

    public static final SuperEnchantOptions DEFAULT = new SuperEnchantOptions(true, true);

}
