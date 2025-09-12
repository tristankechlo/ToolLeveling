package com.tristankechlo.toolleveling.config.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.config.CodecHelper;

public record GeneralOptions(
        long minimumUpgradeCost,
        boolean allowLevelingUselessEnchantments,
        boolean allowLevelingBreakingEnchantments,
        boolean freeUpgradesForCreativePlayers
) {

    public static final Codec<GeneralOptions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    CodecHelper.longRange(1L, Long.MAX_VALUE).fieldOf("minimum_upgrade_cost").forGetter(GeneralOptions::minimumUpgradeCost),
                    Codec.BOOL.fieldOf("allow_leveling_of_useless_enchantments").forGetter(GeneralOptions::allowLevelingUselessEnchantments),
                    Codec.BOOL.fieldOf("allow_leveling_of_breaking_enchantments").forGetter(GeneralOptions::allowLevelingBreakingEnchantments),
                    Codec.BOOL.fieldOf("free_upgrades_for_creative_players").forGetter(GeneralOptions::freeUpgradesForCreativePlayers)
            ).apply(instance, GeneralOptions::new)
    );

    public static final GeneralOptions DEFAULT = new GeneralOptions(1000L, true, true, true);

}
